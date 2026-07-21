/**
 * CineVerse — Seleção de sessão (data/horário/sala) de um filme.
 */

document.addEventListener('DOMContentLoaded', () => {
  const filmeId = sessionStorage.getItem('filmeId');

  if (!filmeId) {
    window.location.href = 'index.html';
    return;
  }

  carregarFilme(filmeId);
  carregarSessoes(filmeId);
});

async function carregarFilme(filmeId) {
  const container = document.getElementById('filme-barra');

  try {
    const filme = await apiFetch(`${API_URL}/filmes/${filmeId}`);
    document.title = `${filme.titulo} — CineVerse`;

    const imagemHTML = filme.posterUrl
      ? `<img class="filme-barra-poster" src="${filme.posterUrl}" alt="Pôster de ${escapeHtml(filme.titulo)}" onerror="this.outerHTML='<div class=\\'filme-barra-poster-placeholder\\'>🎬</div>'">`
      : '<div class="filme-barra-poster-placeholder">🎬</div>';

    container.innerHTML = `
      ${imagemHTML}
      <div class="filme-barra-info">
        <h1>${escapeHtml(filme.titulo)}</h1>
        <div class="filme-barra-meta">
          <span>🎭 ${escapeHtml(filme.genero || '—')}</span>
          <span>⏱ ${filme.duracao ? `${filme.duracao} min` : '—'}</span>
          <span>🔞 ${escapeHtml(filme.classificacao || 'Livre')}</span>
        </div>
        <p class="filme-barra-sinopse">${escapeHtml(filme.sinopse || 'Sinopse não disponível.')}</p>
      </div>
    `;
  } catch (erro) {
    container.innerHTML = `<div class="estado-vazio">${escapeHtml(erro.message)}</div>`;
  }
}

async function carregarSessoes(filmeId) {
  const container = document.getElementById('sessoes-container');

  try {
    const todasSessoes = await apiFetch(`${API_URL}/sessoes`);
    const sessoesDoFilme = todasSessoes
      .filter(s => String(s.filme.id) === String(filmeId))
      .sort((a, b) => (a.data + a.horario).localeCompare(b.data + b.horario));

    if (sessoesDoFilme.length === 0) {
      container.innerHTML = '<div class="estado-vazio">Nenhuma sessão cadastrada para este filme no momento.</div>';
      return;
    }

    const porData = agruparPorData(sessoesDoFilme);
    container.innerHTML = '';

    Object.entries(porData).forEach(([data, sessoes]) => {
      container.appendChild(criarGrupoData(data, sessoes));
    });
  } catch (erro) {
    container.innerHTML = `<div class="estado-vazio">${escapeHtml(erro.message)}</div>`;
  }
}

function agruparPorData(sessoes) {
  return sessoes.reduce((grupos, sessao) => {
    if (!grupos[sessao.data]) grupos[sessao.data] = [];
    grupos[sessao.data].push(sessao);
    return grupos;
  }, {});
}

function criarGrupoData(data, sessoes) {
  const grupo = document.createElement('div');
  grupo.className = 'grupo-data';

  const lista = sessoes.map(sessao => {
    const esgotada = sessao.assentosDisponiveis === 0;
    const poucasVagas = !esgotada && sessao.assentosDisponiveis <= Math.max(5, sessao.assentosTotal * 0.15);

    return `
      <button class="sessao-card ${esgotada ? 'esgotada' : ''}"
              ${esgotada ? 'disabled' : `onclick="selecionarSessao(${sessao.id})"`}>
        <div class="sessao-horario">${sessao.horario}</div>
        <div class="sessao-sala">🎦 ${escapeHtml(sessao.sala.nome)} ${sessao.sala.tipo === 'VIP' ? '<span class="badge badge-vip">VIP</span>' : ''}</div>
        <div class="sessao-preco">${formatarMoeda(sessao.preco)}</div>
        <div class="sessao-vagas ${poucasVagas ? 'pouco' : ''}">
          ${esgotada ? 'Esgotada' : `${sessao.assentosDisponiveis} de ${sessao.assentosTotal} assentos livres`}
        </div>
      </button>
    `;
  }).join('');

  grupo.innerHTML = `
    <div class="grupo-data-titulo">${nomeDiaSemana(data)} — ${formatarDataBr(data)}</div>
    <div class="sessoes-lista">${lista}</div>
  `;

  return grupo;
}

function selecionarSessao(sessaoId) {
  sessionStorage.setItem('sessaoId', sessaoId);
  window.location.href = 'assentos.html';
}
