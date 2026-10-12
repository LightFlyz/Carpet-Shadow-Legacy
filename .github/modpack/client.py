import json, os, re, io, zipfile, hashlib, urllib.request, urllib.parse, time
GV, LOADER = "26.3", "fabric"
UA = {"User-Agent": "LightFlyz/carpet-shadow-legacy client-pack"}
def req(url, raw=False):
    for i in range(4):
        try:
            with urllib.request.urlopen(urllib.request.Request(url, headers=UA), timeout=120) as r:
                return r.read() if raw else json.load(r)
        except urllib.error.HTTPError as e:
            if e.code == 404: return None
            time.sleep(3)
    return None
q = lambda **kw: urllib.parse.urlencode({k: json.dumps(v) if isinstance(v, list) else v for k, v in kw.items()})
def versions(pid, loaders=(LOADER,)):
    return req(f"https://api.modrinth.com/v2/project/{pid}/version?" + q(game_versions=[GV], loaders=list(loaders))) or []

# 1) base: Fabulously Optimized for 26.3
fo = versions("fabulously-optimized", loaders=["fabric", "quilt", "minecraft"]) or versions("fabulously-optimized", loaders=[])
assert fo, "no Fabulously Optimized build for 26.3"
fov = fo[0]
fof = next(f for f in fov["files"] if f["filename"].endswith(".mrpack"))
base = zipfile.ZipFile(io.BytesIO(req(fof["url"], raw=True)))
index = json.loads(base.read("modrinth.index.json"))
have = set()
for f in index["files"]:
    m = re.search(r"/data/([A-Za-z0-9]+)/", f["downloads"][0])
    if m: have.add(m.group(1))
report = [f"BASE Fabulously Optimized {fov['version_number']} ({len(index['files'])} files, deps {index['dependencies']})"]

WANT = [line.split("|") for line in open(".github/modpack/client-mods.txt").read().strip().splitlines()]
def find_project(name, slugs):
    for s in slugs:
        p = req(f"https://api.modrinth.com/v2/project/{s}")
        if p: return p
    hits = (req("https://api.modrinth.com/v2/search?" + q(query=name, limit=5, facets=[["project_type:mod"]])) or {}).get("hits", [])
    norm = lambda t: re.sub(r"[^a-z0-9]", "", t.lower())
    for h in hits:
        if norm(h["title"]).startswith(norm(name)[:8]): return req(f"https://api.modrinth.com/v2/project/{h['project_id']}")
    return None
def env(p):
    side = lambda v: {"required": "required", "optional": "optional", "unsupported": "unsupported"}.get(v, "optional")
    return {"client": "required" if p["client_side"] != "unsupported" else "unsupported", "server": side(p["server_side"])}
def add_version(p, v, why=""):
    f = next((f for f in v["files"] if f["primary"]), v["files"][0])
    index["files"].append({"path": f"mods/{f['filename']}", "hashes": {"sha1": f["hashes"]["sha1"], "sha512": f["hashes"]["sha512"]},
                           "env": env(p), "downloads": [f["url"]], "fileSize": f["size"]})
    have.add(p["id"])
    report.append(f"ADD  {p['title']:30} {v['version_number']:28}{why}")
    for d in v.get("dependencies", []):
        if d["dependency_type"] == "required" and d.get("project_id") and d["project_id"] not in have:
            dp = req(f"https://api.modrinth.com/v2/project/{d['project_id']}")
            dv = versions(d["project_id"])
            if dp and dv: add_version(dp, dv[0], f"  (needed by {p['title']})")
            else: report.append(f"MISS {d['project_id']:30} needed by {p['title']}: no {GV} build")
for name, *slugs in WANT:
    p = find_project(name, slugs)
    if not p: report.append(f"MISS {name:30} not found on Modrinth"); continue
    if p["id"] in have: report.append(f"FO   {p['title']:30} already in Fabulously Optimized"); continue
    vs = versions(p["id"])
    if not vs:
        newest = (req(f"https://api.modrinth.com/v2/project/{p['id']}/version?" + q(loaders=[LOADER])) or [{}])[0].get("game_versions", ["?"])[-1]
        report.append(f"MISS {p['title']:30} no {GV} fabric build (newest supports {newest})"); continue
    add_version(p, vs[0])

# 2) our Carpet Shadow Legacy build (GitHub release URLs are allowed in mrpacks)
jar_url = os.environ["CSL_URL"]; data = req(jar_url, raw=True)
index["files"].append({"path": "mods/" + jar_url.rsplit("/", 1)[1], "hashes": {"sha1": hashlib.sha1(data).hexdigest(), "sha512": hashlib.sha512(data).hexdigest()},
                       "env": {"client": "required", "server": "required"}, "downloads": [jar_url], "fileSize": len(data)})
report.append(f"ADD  {'Carpet Shadow Legacy (classic)':30} {os.environ['CSL_TAG']}")

index["name"] = "Fabulously Optimized + LightFly"
index["versionId"] = f"{fov['version_number']}-lightfly"
index["summary"] = "Fabulously Optimized 26.3 with LightFly's extra mods, Carpet and Carpet Shadow Legacy (classic)"
with zipfile.ZipFile("LightFly-26.3.mrpack", "w", zipfile.ZIP_DEFLATED) as out:
    out.writestr("modrinth.index.json", json.dumps(index, indent=2))
    for n in base.namelist():
        if n != "modrinth.index.json": out.writestr(n, base.read(n))
open("client-report.txt", "w").write("\n".join(report) + "\n"); print("\n".join(report))
