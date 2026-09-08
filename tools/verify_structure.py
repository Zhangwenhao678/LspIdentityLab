#!/usr/bin/env python3
from pathlib import Path
import xml.etree.ElementTree as ET
ROOT = Path(__file__).resolve().parents[1]
checks=[]
def ok(n,c): checks.append((n,bool(c)))
java_init=ROOT/'DemoLsp/src/main/resources/META-INF/xposed/java_init.list'; scope=ROOT/'DemoLsp/src/main/resources/META-INF/xposed/scope.list'; prop=ROOT/'DemoLsp/src/main/resources/META-INF/xposed/module.prop'
ok('java_init exists',java_init.is_file()); ok('scope exists',scope.is_file()); ok('module.prop exists',prop.is_file())
entry=java_init.read_text().strip() if java_init.is_file() else ''; scopes=[x.strip() for x in scope.read_text().splitlines() if x.strip()] if scope.is_file() else []
props={}
if prop.is_file():
 for line in prop.read_text().splitlines():
  if '=' in line:
   k,v=line.split('=',1); props[k.strip()]=v.strip()
ok('single entry',len([x for x in java_init.read_text().splitlines() if x.strip()])==1 if java_init.is_file() else False)
ok('entry matches',entry=='com.example.identitylab.lsp.IdentityHook'); ok('API 102',props.get('minApiVersion')=='102' and props.get('targetApiVersion')=='102'); ok('static scope',props.get('staticScope')=='true'); ok('scope exact',scopes==['com.example.identitylab'])
source=(ROOT/'DemoLsp/src/main/java/com/example/identitylab/lsp/IdentityHook.java').read_text(); manifest=ROOT/'DemoLsp/src/main/AndroidManifest.xml'; app=ET.parse(manifest).getroot().find('application')
ok('XposedModule', 'extends XposedModule' in source); ok('no legacy API','de.robv.android.xposed' not in source); ok('target restricted','TARGET = "com.example.identitylab"' in source); ok('getMachineId hook','getDeclaredMethod("getMachineId")' in source); ok('protective mode','ExceptionMode.PROTECTIVE' in source); ok('no legacy manifest metadata',not any(c.get('{http://schemas.android.com/apk/res/android}name','').startswith('xposed') for c in list(app) if app is not None))
gradle=(ROOT/'DemoLsp/build.gradle').read_text(); ok('API dependency','io.github.libxposed:api:102.0.0' in gradle); ok('namespace','com.example.identitylab.lsp' in gradle); ok('applicationId','com.example.identitylab.lsp' in gradle)
forbidden='com.tunnet.client'; ok('third-party package absent',forbidden not in source and forbidden not in java_init.read_text() and forbidden not in scope.read_text())
failed=[n for n,p in checks if not p]
for n,p in checks: print(f"[{'PASS' if p else 'FAIL'}] {n}")
print(f'Structure checks: {len(checks)-len(failed)}/{len(checks)} passed')
if failed: raise SystemExit('Failed checks: '+', '.join(failed))
