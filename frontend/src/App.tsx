import React, { useState, useEffect } from "react";
import {
  BrowserRouter,
  Routes,
  Route,
  Link,
  useParams,
  useNavigate,
} from "react-router-dom";

/* ------------------------------------------------------------------ */
/* Tipos (espelham os DTOs do backend)                                */
/* ------------------------------------------------------------------ */

type Conta = {
  id: number;
  titular: string;
  saldo: number;
  criadoEm: string;
};

type CriarContaBody = { titular: string; saldoInicial: number };

type Transferencia = {
  id: number;
  contaOrigemId: number;
  contaDestinoId: number;
  valor: number;
  dataTransferencia: string;
  status: string;
};

type TransferenciaBody = {
  contaOrigemId: number;
  contaDestinoId: number;
  valor: number;
};

type Lancamento = {
  id: number;
  contaId: number;
  tipo: string;
  valor: number;
  descricao: string;
  dataMovimento: string;
};

type ErroApi = { mensagem?: string; erro?: string; status?: number };

/* ------------------------------------------------------------------ */
/* Cliente de API                                                     */
/* ------------------------------------------------------------------ */

const API = "/api";

async function requisicao(
  input: RequestInfo | URL,
  init?: RequestInit,
): Promise<Response> {
  const resposta = await fetch(input, {
    headers: { "Content-Type": "application/json", ...(init?.headers || {}) },
    ...init,
  });
  if (!resposta.ok) {
    const corpo: ErroApi = (await resposta.json().catch(() => ({}))) || {};
    const erro = new Error(
      corpo.mensagem || corpo.erro || `Erro ${resposta.status}`,
    );
    (erro as any).status = resposta.status;
    throw erro;
  }
  return resposta;
}

async function listarContas(): Promise<Conta[]> {
  const r = await requisicao(`${API}/contas`);
  return (await r.json()) as Conta[];
}

async function criarConta(body: CriarContaBody): Promise<Conta> {
  const r = await requisicao(`${API}/contas`, {
    method: "POST",
    body: JSON.stringify(body),
  });
  return (await r.json()) as Conta;
}

async function buscarConta(id: number): Promise<Conta> {
  const r = await requisicao(`${API}/contas/${id}`);
  return (await r.json()) as Conta;
}

async function movimentarConta(id: number, tipo: "depositar" | "sacar", valor: number): Promise<Conta> {
  const r = await requisicao(`${API}/contas/${id}/${tipo}`, {
    method: "POST",
    body: JSON.stringify({ valor }),
  });
  return (await r.json()) as Conta;
}

async function transferir(body: TransferenciaBody): Promise<Transferencia> {
  const r = await requisicao(`${API}/transferencias`, {
    method: "POST",
    body: JSON.stringify(body),
  });
  return (await r.json()) as Transferencia;
}

async function listarExtrato(contaId: number): Promise<Lancamento[]> {
  const r = await requisicao(`${API}/extrato/${contaId}`);
  return (await r.json()) as Lancamento[];
}

function moeda(v: number): string {
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  }).format(v);
}

/* ------------------------------------------------------------------ */
/* Layout                                                             */
/* ------------------------------------------------------------------ */

function Layout({ children }: { children: React.ReactNode }) {
  return (
    <div className="max-w-4xl mx-auto p-6">
      <nav className="mb-8 flex gap-4 border-b pb-4">
        <Link to="/" className="px-3 py-2 text-blue-600 font-medium hover:bg-blue-50 rounded">
          Contas
        </Link>
        <Link to="/transferencias" className="px-3 py-2 text-blue-600 font-medium hover:bg-blue-50 rounded">
          Transfer\u00eancias
        </Link>
        <Link to="/extrato" className="px-3 py-2 text-blue-600 font-medium hover:bg-blue-50 rounded">
          Extrato
        </Link>
      </nav>
      <main>{children}</main>
    </div>
  );
}

/* ------------------------------------------------------------------ */
/* Tela: Contas                                                       */
/* ------------------------------------------------------------------ */

function ContasPage() {
  const [contas, setContas] = useState<Conta[]>([]);
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState<string | null>(null);
  const [titular, setTitular] = useState("");
  const [saldoInicial, setSaldoInicial] = useState("");

  const carregar = async () => {
    setCarregando(true);
    setErro(null);
    try {
      setContas(await listarContas());
    } catch (e: any) {
      setErro(e.message);
    } finally {
      setCarregando(false);
    }
  };

  useEffect(() => {
    carregar();
  }, []);

  const handleCriar = async (evt: React.FormEvent) => {
    evt.preventDefault();
    setErro(null);
    try {
      const nova = await criarConta({
        titular,
        saldoInicial: parseFloat(saldoInicial) || 0,
      });
      setContas([...contas, nova]);
      setTitular("");
      setSaldoInicial("");
    } catch (e: any) {
      setErro(e.message);
    }
  };

  const handleMovimentar = async (id: number, tipo: "depositar" | "sacar") => {
    const valor = window.prompt("Valor:");
    if (valor === null || valor === "") return;
    setErro(null);
    try {
      const atualizada = await movimentarConta(id, tipo, parseFloat(valor));
      setContas(contas.map((c) => (c.id === id ? atualizada : c)));
    } catch (e: any) {
      setErro(e.message);
    }
  };

  return (
    <Layout>
      <div className="space-y-6">
        <h1 className="text-2xl font-bold">Contas</h1>

        <form onSubmit={handleCriar} className="grid grid-cols-1 md:grid-cols-3 gap-4 bg-white p-6 rounded-lg shadow border">
          <div>
            <label className="block text-sm font-medium text-slate-700">Titular</label>
            <input
              className="mt-1 block w-full border border-slate-300 rounded-md p-2"
              value={titular}
              onChange={(e) => setTitular(e.target.value)}
              placeholder="Ex.: Ana Silva"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-700">Saldo inicial</label>
            <input
              type="number"
              className="mt-1 block w-full border border-slate-300 rounded-md p-2"
              value={saldoInicial}
              onChange={(e) => setSaldoInicial(e.target.value)}
              placeholder="0,00"
            />
          </div>
          <div className="flex items-end">
            <button
              className="bg-blue-600 text-white px-4 py-2.5 rounded-md hover:bg-blue-700 disabled:opacity-50"
              disabled={!titular || !saldoInicial}
            >
              Criar conta
            </button>
          </div>
        </form>

        {erro && (
          <div className="rounded-md bg-red-50 p-3 border border-red-200 text-red-800 text-sm">
            {erro}
          </div>
        )}

        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold">Minhas contas</h2>
          {carregando ? <span className="text-sm text-slate-500">carregando...</span> : null}
        </div>

        {contas.length === 0 && !carregando && (
          <p className="text-slate-500">Nenhuma conta cadastrada.</p>
        )}

        <div className="space-y-3">
          {contas.map((c) => (
            <div key={c.id} className="flex items-center justify-between bg-white p-4 rounded-lg shadow border">
              <div>
                <div className="font-semibold">{c.titular}</div>
                <div className="text-sm text-slate-500">ID #{c.id} &bull; criada em {c.criadoEm.slice(0, 10)}</div>
              </div>
              <div className="text-right">
                <div className="text-xl font-bold">{moeda(c.saldo)}</div>
                <div className="flex gap-2">
                  <button
                    onClick={() => handleMovimentar(c.id, "depositar")}
                    className="text-xs text-green-700 hover:underline"
                  >
                    Depositar
                  </button>
                  <button
                    onClick={() => handleMovimentar(c.id, "sacar")}
                    className="text-xs text-red-700 hover:underline"
                  >
                    Sacar
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </Layout>
  );
}

/* ------------------------------------------------------------------ */
/* Tela: Transfer\u00eancias                                          */
/* ------------------------------------------------------------------ */

function TransferenciasPage() {
  const [contas, setContas] = useState<Conta[]>([]);
  const [erro, setErro] = useState<string | null>(null);
  const [origem, setOrigem] = useState("");
  const [destino, setDestino] = useState("");
  const [valor, setValor] = useState("");

  const carregar = async () => {
    try {
      setContas(await listarContas());
    } catch (e: any) {
      setErro(e.message);
    } finally {
    }
  };

  // eslint-disable-next-line @typescript-eslint/no-empty-function
  useEffect(() => { carregar(); }, []);

  const handleTransferir = async (evt: React.FormEvent) => {
    evt.preventDefault();
    setErro(null);
    try {
      await transferir({
        contaOrigemId: Number(origem),
        contaDestinoId: Number(destino),
        valor: parseFloat(valor),
      });
      setOrigem("");
      setDestino("");
      setValor("");
      await carregar();
    } catch (e: any) {
      setErro(e.message);
    }
  };

  return (
    <Layout>
      <div className="space-y-6">
        <h1 className="text-2xl font-bold">Transfer\u00eancias</h1>

        <form onSubmit={handleTransferir} className="grid grid-cols-1 md:grid-cols-4 gap-4 bg-white p-6 rounded-lg shadow border">
          <div>
            <label className="block text-sm font-medium text-slate-700">Conta origem</label>
            <select
              className="mt-1 block w-full border border-slate-300 rounded-md p-2"
              value={origem}
              onChange={(e) => setOrigem(e.target.value)}
            >
              <option value="">Escolha</option>
              {contas.map((c) => (
                <option key={c.id} value={c.id}>
                  #{c.id} - {c.titular} ({moeda(c.saldo)})
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-700">Conta destino</label>
            <select
              className="mt-1 block w-full border border-slate-300 rounded-md p-2"
              value={destino}
              onChange={(e) => setDestino(e.target.value)}
            >
              <option value="">Escolha</option>
              {contas.map((c) => (
                <option key={c.id} value={c.id}>
                  #{c.id} - {c.titular} ({moeda(c.saldo)})
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-700">Valor</label>
            <input
              type="number"
              className="mt-1 block w-full border border-slate-300 rounded-md p-2"
              value={valor}
              onChange={(e) => setValor(e.target.value)}
              placeholder="0,00"
            />
          </div>
          <div className="flex items-end">
            <button
              className="bg-indigo-600 text-white px-4 py-2.5 rounded-md hover:bg-indigo-700 disabled:opacity-50"
              disabled={!origem || !destino || !valor || origem === destino}
            >
              Transferir
            </button>
          </div>
        </form>

        {erro && (
          <div className="rounded-md bg-red-50 p-3 border border-red-200 text-red-800 text-sm">
            {erro}
          </div>
        )}
      </div>
    </Layout>
  );
}

/* ------------------------------------------------------------------ */
/* Tela: Extrato                                                      */
/* ------------------------------------------------------------------ */

function ExtratoPage() {
  const { contaId } = useParams<{ contaId: string }>();
  const navigate = useNavigate();
  const [lancamentos, setLancamentos] = useState<Lancamento[]>([]);
  const [conta, setConta] = useState<Conta | null>(null);
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState<string | null>(null);

  const carregar = async () => {
    setCarregando(true);
    setErro(null);
    try {
      if (!contaId) throw new Error("Conta n\u00e3o informada");
      const id = Number(contaId);
      const [c, l] = await Promise.all([buscarConta(id), listarExtrato(id)]);
      setConta(c);
      setLancamentos(l);
    } catch (e: any) {
      setErro(e.message);
    } finally {
      setCarregando(false);
    }
  };

  // eslint-disable-next-line @typescript-eslint/no-empty-function
  React.useEffect(() => { carregar(); }, [contaId]);

  const voltar = () => navigate("/");

  return (
    <Layout>
      <div className="space-y-6">
        <div className="flex items-center gap-3">
          <button onClick={voltar} className="text-sm text-blue-600 hover:underline">
            &larr; Voltar
          </button>
          <h1 className="text-2xl font-bold">Extrato</h1>
        </div>

        {carregando && <p className="text-sm text-slate-500">carregando...</p>}

        {!carregando && erro && (
          <div className="rounded-md bg-red-50 p-3 border border-red-200 text-red-800 text-sm">
            {erro}
          </div>
        )}

        {!carregando && !erro && conta && (
          <div>
            <div className="bg-white p-6 rounded-lg shadow border">
              <div className="flex items-center justify-between mb-4">
                <div>
                  <div className="font-semibold">{conta.titular}</div>
                  <div className="text-sm text-slate-500">Conta #{conta.id}</div>
                </div>
                <div className="text-2xl font-bold">{moeda(conta.saldo)}</div>
              </div>
            </div>

            <h2 className="text-lg font-semibold">Lan\u00e7amentos</h2>
            {lancamentos.length === 0 ? (
              <p className="text-slate-500">Sem lan\u00e7amentos.</p>
            ) : (
              <div className="bg-white rounded-lg shadow border overflow-hidden">
                <table className="w-full text-left">
                  <thead>
                    <tr className="bg-slate-50 border-b">
                      <th className="px-4 py-3 text-sm font-medium text-slate-600">Data</th>
                      <th className="px-4 py-3 text-sm font-medium text-slate-600">Tipo</th>
                      <th className="px-4 py-3 text-sm font-medium text-slate-600">Descri\u00e7\u00e3o</th>
                      <th className="px-4 py-3 text-sm font-medium text-slate-600 text-right">Valor</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y">
                    {lancamentos.map((l) => (
                      <tr key={l.id} className={l.tipo === "CREDITO" ? "bg-green-50" : ""}>
                        <td className="px-4 py-3 text-sm text-slate-600">
                          {l.dataMovimento.slice(0, 16).replace("T", " ")}
                        </td>
                        <td className="px-4 py-3 text-sm font-medium">
                          {l.tipo === "CREDITO" ? (
                            <span className="text-green-700">Cr\u00e9dito</span>
                          ) : (
                            <span className="text-red-700">D\u00e9bito</span>
                          )}
                        </td>
                        <td className="px-4 py-3 text-sm text-slate-700">{l.descricao}</td>
                        <td className="px-4 py-3 text-sm text-right font-mono">
                          {l.tipo === "CREDITO" ? "+" : "-"}{moeda(l.valor)}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
            </div>
          )}
      </div>
    </Layout>
  );
}

/* ------------------------------------------------------------------ */
/* App                                                                */
/* ------------------------------------------------------------------ */

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<ContasPage />} />
        <Route path="/transferencias" element={<TransferenciasPage />} />
        <Route path="/extrato/:contaId" element={<ExtratoPage />} />
      </Routes>
    </BrowserRouter>
  );
}
