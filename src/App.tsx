import { useMemo, useState } from 'react';
import { permissionBroker, permissionCatalog } from './permissions';
import type { CapabilityTestResult, PermissionDiagnostics, PermissionId } from './permissions';

type DiagnosticsMap = Partial<Record<PermissionId, PermissionDiagnostics>>;
type TestMap = Partial<Record<PermissionId, CapabilityTestResult>>;

export function App() {
  const [diagnostics, setDiagnostics] = useState<DiagnosticsMap>({});
  const [tests, setTests] = useState<TestMap>({});
  const [busy, setBusy] = useState<string | null>(null);

  const grouped = useMemo(() => {
    return permissionCatalog.reduce<Record<string, typeof permissionCatalog>>((acc, item) => {
      acc[item.category] ??= [];
      acc[item.category].push(item);
      return acc;
    }, {});
  }, []);

  async function run<T>(label: string, fn: () => Promise<T>): Promise<T | undefined> {
    setBusy(label);
    try {
      return await fn();
    } catch (error) {
      alert(error instanceof Error ? error.message : String(error));
      return undefined;
    } finally {
      setBusy(null);
    }
  }

  async function check(id: PermissionId) {
    const result = await run(`check:${id}`, () => permissionBroker.check(id));
    if (result) setDiagnostics((old) => ({ ...old, [id]: result }));
  }

  async function request(id: PermissionId) {
    const result = await run(`request:${id}`, () => permissionBroker.request(id));
    if (result) setDiagnostics((old) => ({ ...old, [id]: result }));
  }

  async function openSettings(id: PermissionId) {
    await run(`settings:${id}`, () => permissionBroker.openSettings(id));
  }

  async function test(id: PermissionId) {
    const result = await run(`test:${id}`, () => permissionBroker.test(id));
    if (result) setTests((old) => ({ ...old, [id]: result }));
  }

  async function copyReport() {
    const report = {
      createdAt: new Date().toISOString(),
      userAgent: navigator.userAgent,
      diagnostics,
      tests,
    };
    await navigator.clipboard.writeText(JSON.stringify(report, null, 2));
    alert('Diagnostics copied to clipboard.');
  }

  return (
    <main className="page">
      <header className="hero">
        <p className="eyebrow">Permission Lab</p>
        <h1>Android / Web Permission Lab by GPT</h1>
        <p>
          A debug-first permission playground for React + Capacitor apps. It models runtime permissions,
          Android special access, and China ROM manual settings separately.
        </p>
        <button onClick={copyReport} className="primary">Copy diagnostics JSON</button>
      </header>

      {Object.entries(grouped).map(([category, items]) => (
        <section key={category} className="section">
          <h2>{category}</h2>
          <div className="grid">
            {items.map((item) => {
              const d = diagnostics[item.id];
              const t = tests[item.id];
              return (
                <article key={item.id} className="card">
                  <div className="cardTop">
                    <div>
                      <h3>{item.title}</h3>
                      <code>{item.id}</code>
                    </div>
                    <span className={`badge ${d?.state ?? 'unknown'}`}>{d?.state ?? 'unknown'}</span>
                  </div>
                  <p>{item.description}</p>
                  {item.androidPermissions && (
                    <pre>{item.androidPermissions.join('\n')}</pre>
                  )}
                  {item.androidSpecialAccess && <pre>{item.androidSpecialAccess}</pre>}
                  {item.oemNotes && <p className="note">{item.oemNotes}</p>}

                  <div className="actions">
                    <button disabled={!!busy} onClick={() => check(item.id)}>Check</button>
                    <button disabled={!!busy} onClick={() => request(item.id)}>Request</button>
                    <button disabled={!!busy} onClick={() => openSettings(item.id)}>Settings</button>
                    <button disabled={!!busy} onClick={() => test(item.id)}>Real test</button>
                  </div>

                  {d && <pre className="json">{JSON.stringify(d, null, 2)}</pre>}
                  {t && <pre className={`json ${t.ok ? 'ok' : 'fail'}`}>{JSON.stringify(t, null, 2)}</pre>}
                </article>
              );
            })}
          </div>
        </section>
      ))}
    </main>
  );
}
