import json, os, sys, hashlib, urllib.request, urllib.parse, time
GV, LOADER = "26.3", "fabric"
# (what's on the 1.20.1 server, candidate Modrinth slugs)
MODS = [
 ("Dynmap", ["dynmap"]),
 ("KeepHeadNames", ["keepheadnames", "keep-head-names"]),
 ("Carpet-Fixes", ["carpet-fixes"]),
 ("Emotecraft", ["emotecraft"]),
 ("Fabric API", ["fabric-api"]),
 ("Carpet", ["carpet"]),
 ("Fabric Language Kotlin", ["fabric-language-kotlin"]),
 ("FerriteCore", ["ferrite-core"]),
 ("Krypton", ["krypton"]),
 ("LazyDFU", ["lazydfu"]),
 ("Let Me Despawn", ["lmd", "let-me-despawn", "letmedespawn"]),
 ("Lithium", ["lithium"]),
 ("Placeholder API", ["placeholder-api"]),
 ("Plasmo Voice", ["plasmo-voice"]),
 ("ServerCore", ["servercore"]),
 ("spark", ["spark"]),
 ("Starlight", ["starlight"]),
 ("Styled Chat", ["styled-chat"]),
 ("Styled Player List", ["styledplayerlist", "styled-player-list"]),
 ("Textile Backup", ["textile_backup", "textile-backup"]),
 ("VMP", ["vmp-fabric"]),
 ("Cloth Config", ["cloth-config"]),
]
UA = {"User-Agent": "LightFlyz/carpet-shadow-legacy modpack-resolver"}
def get(url):
    for i in range(3):
        try:
            with urllib.request.urlopen(urllib.request.Request(url, headers=UA), timeout=30) as r:
                return json.load(r)
        except urllib.error.HTTPError as e:
            if e.code == 404: return None
            time.sleep(2)
    return None
def versions(slug):
    q = urllib.parse.urlencode({"game_versions": json.dumps([GV]), "loaders": json.dumps([LOADER])})
    return get(f"https://api.modrinth.com/v2/project/{slug}/version?{q}")
out = os.environ.get("OUT", "pack"); os.makedirs(out, exist_ok=True)
report, picked, seen = [], {}, set()
def take(name, slug, v, via=""):
    if v["project_id"] in seen: return
    seen.add(v["project_id"])
    f = next((f for f in v["files"] if f["primary"]), v["files"][0])
    data = urllib.request.urlopen(urllib.request.Request(f["url"], headers=UA), timeout=120).read()
    assert hashlib.sha512(data).hexdigest() == f["hashes"]["sha512"], f["filename"]
    open(os.path.join(out, f["filename"]), "wb").write(data)
    report.append(f"OK   {name:24} {slug:24} {v['version_number']:28} {f['filename']}{via}")
    for d in v.get("dependencies", []):
        if d["dependency_type"] == "required" and d.get("project_id") and d["project_id"] not in seen:
            dv = versions(d["project_id"])
            if dv: 
                proj = get(f"https://api.modrinth.com/v2/project/{d['project_id']}")
                take(proj["title"] if proj else d["project_id"], proj["slug"] if proj else d["project_id"], dv[0], f"  (required by {name})")
            else:
                report.append(f"MISS {'dependency '+d['project_id']:24} required by {name}: no {GV} build")
for name, slugs in MODS:
    found = False
    for slug in slugs:
        proj = get(f"https://api.modrinth.com/v2/project/{slug}")
        if not proj: continue
        vs = versions(slug)
        if vs:
            take(name, slug, vs[0]); found = True; break
        latest = get(f"https://api.modrinth.com/v2/project/{slug}/version?" + urllib.parse.urlencode({"loaders": json.dumps([LOADER])})) or []
        lv = latest[0]["game_versions"][-1] if latest else "?"
        report.append(f"MISS {name:24} {slug:24} no {GV} fabric build (newest supports {lv})"); found = True; break
    if not found:
        report.append(f"MISS {name:24} {'?':24} not found on Modrinth")
open("modpack-report.txt", "w").write("\n".join(report) + "\n")
print("\n".join(report))
