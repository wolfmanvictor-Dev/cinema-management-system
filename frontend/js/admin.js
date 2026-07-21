/**
 * CineVerse — Painel administrativo.
 */

const TIPOS_SALA_LABEL = { PEQUENA: 'Pequena', MEDIA: 'Média', GRANDE: 'Grande', VIP: 'VIP' };

// Layout sugerido ao escolher um tipo de sala (fileiras, assentos por fileira)
const LAYOUT_SUGERIDO = {
  PEQUENA: [4, 6],
  MEDIA: [6, 8],
  GRANDE: [8, 12],
  VIP: [4, 5]
};

let cacheFilmes = [];
let cacheSalas = [];

document.addEventListener('DOMContentLoaded', () => {
  if (!estaAutenticado()) {
    redirecionarParaLogin();
    return;
  }

  document.getElementById('admin-nome').textContent = sessionStorage.getItem('adminNome') || 'Administrador';

  carregarDashboard();
  carregarFilmesAdmin();
  carregarSalasAdmin();
  carregarSessoesAdmin();
  carregarCompras();
});

// ---------------------------------------------------------------------------
// Autenticação
// ---------------------------------------------------------------------------

function estaAutenticado() {
  return Boolean(sessionStorage.getItem('adminToken'));
}

function authHeader() {
  return {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${sessionStorage.getItem('adminToken')}`
  };
}

async function apiAdmin(url, opcoes = {}) {
  try {
    return await apiFetch(url, { ...opcoes, headers: { ...authHeader(), ...opcoes.headers } });
  } catch (erro) {
    if (erro.message.includes('Sessão expirada')) {
      encerrarSessao();
    }
    throw erro;
  }
}

function encerrarSessao() {
  sessionStorage.removeItem('adminToken');
  sessionStorage.removeItem('adminNome');
  redirecionarParaLogin();
}

function redirecionarParaLogin() {
  window.location.href = 'login.html';
}

function sair() {
  encerrarSessao();
}

// ---------------------------------------------------------------------------
// Navegação entre abas
// ---------------------------------------------------------------------------

function trocarAba(aba, botao) {
  document.querySelectorAll('.aba').forEach(el => el.classList.remove('ativa'));
  document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));

  document.getElementById(`aba-${aba}`).classList.add('ativa');
  botao.classList.add('active');
}

// ---------------------------------------------------------------------------
// Dashboard
// ---------------------------------------------------------------------------

async function carregarDashboard() {
  try {
    const dados = await apiAdmin(`${API_URL}/admin/dashboard`);

    document.getElementById('total-filmes').textContent = dados.totalFilmes ?? '—';
    document.getElementById('total-sessoes').textContent = dados.totalSessoes ?? '—';
    document.getElementById('total-compras').textContent = dados.totalCompras ?? '—';
    document.getElementById('total-receita').textContent = dados.receita != null ? formatarMoeda(dados.receita) : '—';
    document.getElementById('assentos-ocupados').textContent = dados.assentosOcupados ?? '—';

    renderizarUltimasCompras(dados.ultimasCompras || []);
  } catch (erro) {
    toast(erro.message, 'erro');
  }
}

function renderizarUltimasCompras(compras) {
  const tbody = document.getElementById('tabela-ultimas-compras');

  if (compras.length === 0) {
    tbody.innerHTML = '<tr><td colspan="6" class="loading-cell">Nenhuma compra realizada.</td></tr>';
    return;
  }

  tbody.innerHTML = compras.map(compra => `
    <tr>
      <td><code>${escapeHtml(compra.codigo)}</code></td>
      <td>${escapeHtml(compra.compradorNome)}</td>
      <td>${escapeHtml(compra.filmeNome)}</td>
      <td>${escapeHtml(compra.assentos)}</td>
      <td>${formatarMoeda(compra.total)}</td>
      <td>${formatarDataHoraBr(compra.dataCriacao)}</td>
    </tr>
  `).join('');
}

// ---------------------------------------------------------------------------
// Filmes
// ---------------------------------------------------------------------------

async function carregarFilmesAdmin() {
  const tbody = document.getElementById('tabela-filmes');

  try {
    cacheFilmes = await apiFetch(`${API_URL}/filmes`);
    preencherSelectFilmes();

    if (cacheFilmes.length === 0) {
      tbody.innerHTML = '<tr><td colspan="6" class="loading-cell">Nenhum filme cadastrado.</td></tr>';
      return;
    }

    tbody.innerHTML = cacheFilmes.map(filme => `
      <tr>
        <td>${filme.id}</td>
        <td>${escapeHtml(filme.titulo)}</td>
        <td>${escapeHtml(filme.genero || '—')}</td>
        <td>${filme.duracao ? `${filme.duracao} min` : '—'}</td>
        <td>${escapeHtml(filme.classificacao || 'Livre')}</td>
        <td>
          <button class="btn-acao" onclick="editarFilme(${filme.id})">Editar</button>
          <button class="btn-acao danger" onclick="excluirFilme(${filme.id})">Excluir</button>
        </td>
      </tr>
    `).join('');
  } catch (erro) {
    tbody.innerHTML = `<tr><td colspan="6" class="loading-cell">${escapeHtml(erro.message)}</td></tr>`;
  }
}

function abrirModalFilme() {
  document.getElementById('modal-filme-titulo').textContent = 'Novo Filme';
  document.getElementById('filme-id').value = '';
  document.getElementById('filme-titulo').value = '';
  document.getElementById('filme-genero').value = '';
  document.getElementById('filme-classificacao').value = 'Livre';
  document.getElementById('filme-duracao').value = '';
  document.getElementById('filme-poster').value = '';
  document.getElementById('filme-sinopse').value = '';
  document.getElementById('modal-filme').style.display = 'flex';
}

function editarFilme(id) {
  const filme = cacheFilmes.find(f => f.id === id);
  if (!filme) return;

  document.getElementById('modal-filme-titulo').textContent = 'Editar Filme';
  document.getElementById('filme-id').value = filme.id;
  document.getElementById('filme-titulo').value = filme.titulo;
  document.getElementById('filme-genero').value = filme.genero || '';
  document.getElementById('filme-classificacao').value = filme.classificacao || 'Livre';
  document.getElementById('filme-duracao').value = filme.duracao || '';
  document.getElementById('filme-poster').value = filme.posterUrl || '';
  document.getElementById('filme-sinopse').value = filme.sinopse || '';
  document.getElementById('modal-filme').style.display = 'flex';
}

function fecharModalFilme() {
  document.getElementById('modal-filme').style.display = 'none';
}

async function salvarFilme() {
  const id = document.getElementById('filme-id').value;
  const body = {
    titulo: document.getElementById('filme-titulo').value.trim(),
    genero: document.getElementById('filme-genero').value.trim(),
    classificacao: document.getElementById('filme-classificacao').value,
    duracao: parseInt(document.getElementById('filme-duracao').value, 10) || null,
    posterUrl: document.getElementById('filme-poster').value.trim(),
    sinopse: document.getElementById('filme-sinopse').value.trim()
  };

  const url = id ? `${API_URL}/filmes/${id}` : `${API_URL}/filmes`;
  const method = id ? 'PUT' : 'POST';

  try {
    await apiAdmin(url, { method, body: JSON.stringify(body) });
    toast(id ? 'Filme atualizado com sucesso.' : 'Filme cadastrado com sucesso.', 'sucesso');
    fecharModalFilme();
    carregarFilmesAdmin();
  } catch (erro) {
    toast(erro.message, 'erro');
  }
}

async function excluirFilme(id) {
  if (!confirm('Tem certeza que deseja excluir este filme?')) return;

  try {
    await apiAdmin(`${API_URL}/filmes/${id}`, { method: 'DELETE' });
    toast('Filme excluído.', 'sucesso');
    carregarFilmesAdmin();
  } catch (erro) {
    toast(erro.message, 'erro');
  }
}

function preencherSelectFilmes() {
  const select = document.getElementById('sessao-filme');
  const valorAtual = select.value;
  select.innerHTML = '<option value="">Selecione o filme...</option>' +
    cacheFilmes.map(f => `<option value="${f.id}">${escapeHtml(f.titulo)}</option>`).join('');
  select.value = valorAtual;
}

// ---------------------------------------------------------------------------
// Salas
// ---------------------------------------------------------------------------

async function carregarSalasAdmin() {
  const tbody = document.getElementById('tabela-salas');

  try {
    cacheSalas = await apiFetch(`${API_URL}/salas`);
    preencherSelectSalas();

    if (cacheSalas.length === 0) {
      tbody.innerHTML = '<tr><td colspan="7" class="loading-cell">Nenhuma sala cadastrada.</td></tr>';
      return;
    }

    tbody.innerHTML = cacheSalas.map(sala => `
      <tr>
        <td>${sala.id}</td>
        <td>${escapeHtml(sala.nome)}</td>
        <td><span class="badge ${sala.tipo === 'VIP' ? 'badge-vip' : 'badge-livre'}">${TIPOS_SALA_LABEL[sala.tipo] || sala.tipo}</span></td>
        <td>${sala.fileiras}</td>
        <td>${sala.assentosPorFileira}</td>
        <td>${sala.fileiras * sala.assentosPorFileira}</td>
        <td>
          <button class="btn-acao" onclick="editarSala(${sala.id})">Editar</button>
          <button class="btn-acao danger" onclick="excluirSala(${sala.id})">Excluir</button>
        </td>
      </tr>
    `).join('');
  } catch (erro) {
    tbody.innerHTML = `<tr><td colspan="7" class="loading-cell">${escapeHtml(erro.message)}</td></tr>`;
  }
}

function abrirModalSala() {
  document.getElementById('modal-sala-titulo').textContent = 'Nova Sala';
  document.getElementById('sala-id').value = '';
  document.getElementById('sala-nome').value = '';
  document.getElementById('sala-tipo').value = 'PEQUENA';
  document.getElementById('sala-fileiras').value = LAYOUT_SUGERIDO.PEQUENA[0];
  document.getElementById('sala-assentos').value = LAYOUT_SUGERIDO.PEQUENA[1];
  atualizarPreviewCapacidade();
  document.getElementById('modal-sala').style.display = 'flex';
}

function editarSala(id) {
  const sala = cacheSalas.find(s => s.id === id);
  if (!sala) return;

  document.getElementById('modal-sala-titulo').textContent = 'Editar Sala';
  document.getElementById('sala-id').value = sala.id;
  document.getElementById('sala-nome').value = sala.nome;
  document.getElementById('sala-tipo').value = sala.tipo;
  document.getElementById('sala-fileiras').value = sala.fileiras;
  document.getElementById('sala-assentos').value = sala.assentosPorFileira;
  atualizarPreviewCapacidade();
  document.getElementById('modal-sala').style.display = 'flex';
}

function fecharModalSala() {
  document.getElementById('modal-sala').style.display = 'none';
}

function sugerirLayoutSala() {
  const tipo = document.getElementById('sala-tipo').value;
  const [fileiras, assentos] = LAYOUT_SUGERIDO[tipo] || [4, 6];
  document.getElementById('sala-fileiras').value = fileiras;
  document.getElementById('sala-assentos').value = assentos;
  atualizarPreviewCapacidade();
}

function atualizarPreviewCapacidade() {
  const fileiras = parseInt(document.getElementById('sala-fileiras').value, 10) || 0;
  const assentos = parseInt(document.getElementById('sala-assentos').value, 10) || 0;
  document.getElementById('sala-capacidade-preview').textContent = fileiras * assentos;
}

document.addEventListener('input', (evento) => {
  if (evento.target.id === 'sala-fileiras' || evento.target.id === 'sala-assentos') {
    atualizarPreviewCapacidade();
  }
});

async function salvarSala() {
  const id = document.getElementById('sala-id').value;
  const body = {
    nome: document.getElementById('sala-nome').value.trim(),
    tipo: document.getElementById('sala-tipo').value,
    fileiras: parseInt(document.getElementById('sala-fileiras').value, 10),
    assentosPorFileira: parseInt(document.getElementById('sala-assentos').value, 10)
  };

  const url = id ? `${API_URL}/salas/${id}` : `${API_URL}/salas`;
  const method = id ? 'PUT' : 'POST';

  try {
    await apiAdmin(url, { method, body: JSON.stringify(body) });
    toast(id ? 'Sala atualizada com sucesso.' : 'Sala cadastrada com sucesso.', 'sucesso');
    fecharModalSala();
    carregarSalasAdmin();
  } catch (erro) {
    toast(erro.message, 'erro');
  }
}

async function excluirSala(id) {
  if (!confirm('Tem certeza que deseja excluir esta sala?')) return;

  try {
    await apiAdmin(`${API_URL}/salas/${id}`, { method: 'DELETE' });
    toast('Sala excluída.', 'sucesso');
    carregarSalasAdmin();
  } catch (erro) {
    toast(erro.message, 'erro');
  }
}

function preencherSelectSalas() {
  const select = document.getElementById('sessao-sala');
  const valorAtual = select.value;
  select.innerHTML = '<option value="">Selecione a sala...</option>' +
    cacheSalas.map(s => `<option value="${s.id}">${escapeHtml(s.nome)} (${TIPOS_SALA_LABEL[s.tipo] || s.tipo})</option>`).join('');
  select.value = valorAtual;
}

// ---------------------------------------------------------------------------
// Sessões
// ---------------------------------------------------------------------------

let cacheSessoes = [];

async function carregarSessoesAdmin() {
  const tbody = document.getElementById('tabela-sessoes');

  try {
    cacheSessoes = await apiFetch(`${API_URL}/sessoes`);

    if (cacheSessoes.length === 0) {
      tbody.innerHTML = '<tr><td colspan="7" class="loading-cell">Nenhuma sessão cadastrada.</td></tr>';
      return;
    }

    const ordenadas = [...cacheSessoes].sort((a, b) => (a.data + a.horario).localeCompare(b.data + b.horario));

    tbody.innerHTML = ordenadas.map(sessao => `
      <tr>
        <td>${escapeHtml(sessao.filme.titulo)}</td>
        <td>${escapeHtml(sessao.sala.nome)}</td>
        <td>${formatarDataBr(sessao.data)}</td>
        <td>${sessao.horario}</td>
        <td>${formatarMoeda(sessao.preco)}</td>
        <td>${sessao.assentosTotal - sessao.assentosDisponiveis} / ${sessao.assentosTotal}</td>
        <td>
          <button class="btn-acao" onclick="editarSessao(${sessao.id})">Editar</button>
          <button class="btn-acao danger" onclick="excluirSessao(${sessao.id})">Excluir</button>
        </td>
      </tr>
    `).join('');
  } catch (erro) {
    tbody.innerHTML = `<tr><td colspan="7" class="loading-cell">${escapeHtml(erro.message)}</td></tr>`;
  }
}

function abrirModalSessao() {
  document.getElementById('modal-sessao-titulo').textContent = 'Nova Sessão';
  document.getElementById('sessao-id').value = '';
  document.getElementById('sessao-filme').value = '';
  document.getElementById('sessao-sala').value = '';
  document.getElementById('sessao-data').value = '';
  document.getElementById('sessao-horario').value = '';
  document.getElementById('sessao-preco').value = '';
  document.getElementById('modal-sessao').style.display = 'flex';
}

function editarSessao(id) {
  const sessao = cacheSessoes.find(s => s.id === id);
  if (!sessao) return;

  document.getElementById('modal-sessao-titulo').textContent = 'Editar Sessão';
  document.getElementById('sessao-id').value = sessao.id;
  document.getElementById('sessao-filme').value = sessao.filme.id;
  document.getElementById('sessao-sala').value = sessao.sala.id;
  document.getElementById('sessao-data').value = sessao.data;
  document.getElementById('sessao-horario').value = sessao.horario;
  document.getElementById('sessao-preco').value = sessao.preco;
  document.getElementById('modal-sessao').style.display = 'flex';
}

function fecharModalSessao() {
  document.getElementById('modal-sessao').style.display = 'none';
}

async function salvarSessao() {
  const id = document.getElementById('sessao-id').value;
  const body = {
    filmeId: parseInt(document.getElementById('sessao-filme').value, 10) || null,
    salaId: parseInt(document.getElementById('sessao-sala').value, 10) || null,
    data: document.getElementById('sessao-data').value,
    horario: document.getElementById('sessao-horario').value,
    preco: parseFloat(document.getElementById('sessao-preco').value)
  };

  const url = id ? `${API_URL}/sessoes/${id}` : `${API_URL}/sessoes`;
  const method = id ? 'PUT' : 'POST';

  try {
    await apiAdmin(url, { method, body: JSON.stringify(body) });
    toast(id ? 'Sessão atualizada com sucesso.' : 'Sessão cadastrada com sucesso.', 'sucesso');
    fecharModalSessao();
    carregarSessoesAdmin();
    carregarDashboard();
  } catch (erro) {
    toast(erro.message, 'erro');
  }
}

async function excluirSessao(id) {
  if (!confirm('Tem certeza que deseja excluir esta sessão?')) return;

  try {
    await apiAdmin(`${API_URL}/sessoes/${id}`, { method: 'DELETE' });
    toast('Sessão excluída.', 'sucesso');
    carregarSessoesAdmin();
    carregarDashboard();
  } catch (erro) {
    toast(erro.message, 'erro');
  }
}

// ---------------------------------------------------------------------------
// Compras
// ---------------------------------------------------------------------------

async function carregarCompras() {
  const tbody = document.getElementById('tabela-compras');

  try {
    const compras = await apiAdmin(`${API_URL}/compras`);

    if (compras.length === 0) {
      tbody.innerHTML = '<tr><td colspan="9" class="loading-cell">Nenhuma compra registrada.</td></tr>';
      return;
    }

    tbody.innerHTML = compras.map(compra => `
      <tr>
        <td><code>${escapeHtml(compra.codigo)}</code></td>
        <td>${escapeHtml(compra.compradorNome)}</td>
        <td>${escapeHtml(compra.compradorEmail)}</td>
        <td>${escapeHtml(compra.filmeTitulo)}</td>
        <td>${escapeHtml(compra.salaNome)}</td>
        <td>${escapeHtml(compra.assentos)}</td>
        <td>${formatarMoeda(compra.total)}</td>
        <td>${escapeHtml(compra.metodoPagamento)}</td>
        <td>${formatarDataHoraBr(compra.dataCriacao)}</td>
      </tr>
    `).join('');
  } catch (erro) {
    tbody.innerHTML = `<tr><td colspan="9" class="loading-cell">${escapeHtml(erro.message)}</td></tr>`;
  }
}
