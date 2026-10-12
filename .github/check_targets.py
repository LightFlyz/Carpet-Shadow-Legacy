# Verify that every mixin target in MixinMouse exists in 26.3's MouseHandler bytecode.
import re, sys
src = open(sys.argv[1]).read()
mixin = open("src/main/java/me/virb3/mcmouser/client/mixin/MixinMouse.java").read()
ok = True
for m in sorted(set(re.findall(r'method\s*=\s*"([A-Za-z0-9_$]+)', mixin))):
    found = re.search(r'\b' + re.escape(m) + r'\(', src) is not None
    print(f"method {m}: {'OK' if found else 'MISSING'}"); ok &= found
for t in re.findall(r'target\s*=\s*"L([^;]+);([A-Za-z0-9_$]+):', mixin):
    owner, field = t[0].replace('/', '.'), t[1]
    found = (owner + "." + field) in src
    print(f"field {owner}.{field}: {'OK' if found else 'MISSING'}"); ok &= found
sys.exit(0 if ok else 1)
