/**
 * CineVerse — Tela inicial (catálogo de filmes).
 */

let todosOsFilmes = [];

document.addEventListener('DOMContentLoaded', carregarFilmes);

async function carregarFilmes() {
  const grid = document.getElementById('filmes-grid');
  const mensagemErro = document.getElementById('error-msg');
  mensagemErro.style.display = 'none';

  try {
    todosOsFilmes = await apiFetch(`${API_URL}/filmes`);

    if (todosOsFilmes.length === 0) {
      grid.innerHTML = '<div class="loading-msg">Nenhum filme em cartaz no momento.</div>';
      return;
    }

    montarFiltroGenero(todosOsFilmes);
    renderizarFilmes(todosOsFilmes);
  } catch (erro) {
    grid.innerHTML = '';
    mensagemErro.textContent = erro.message;
    mensagemErro.style.display = 'block';
  }
}

function montarFiltroGenero(filmes) {
  const select = document.getElementById('filtro-genero');
  const generos = [...new Set(filmes.map(f => f.genero).filter(Boolean))].sort();

  generos.forEach(genero => {
    const option = document.createElement('option');
    option.value = genero;
    option.textContent = genero;
    select.appendChild(option);
  });
}

function aplicarFiltro() {
  const generoSelecionado = document.getElementById('filtro-genero').value;
  const filmesFiltrados = generoSelecionado
    ? todosOsFilmes.filter(f => f.genero === generoSelecionado)
    : todosOsFilmes;

  renderizarFilmes(filmesFiltrados);
}

function renderizarFilmes(filmes) {
  const grid = document.getElementById('filmes-grid');

  if (filmes.length === 0) {
    grid.innerHTML = '<div class="loading-msg">Nenhum filme encontrado para esse gênero.</div>';
    return;
  }

  grid.innerHTML = '';
  filmes.forEach(filme => grid.appendChild(criarCardFilme(filme)));
}

function criarCardFilme(filme) {
  const card = document.createElement('article');
  card.className = 'filme-card';

  const imagemHTML = filme.posterUrl
    ? `<img class="filme-poster" src="${filme.posterUrl}" alt="Pôster de ${escapeHtml(filme.titulo)}" loading="lazy" onerror="this.parentElement.innerHTML='<div class=\\'poster-placeholder\\'>🎬</div>'">`
    : '<div class="poster-placeholder" aria-hidden="true">🎬</div>';

  card.innerHTML = `
    <div class="filme-poster-wrap">
      ${imagemHTML}
      <span class="filme-classificacao">${filme.classificacao || 'Livre'}</span>
    </div>
    <div class="filme-info">
      <h3 class="filme-titulo" title="${escapeHtml(filme.titulo)}">${escapeHtml(filme.titulo)}</h3>
      <div class="filme-meta">
        <span>🎭 ${escapeHtml(filme.genero || '—')}</span>
        <span>⏱ ${filme.duracao ? `${filme.duracao} min` : '—'}</span>
      </div>
      <div class="filme-canhoto">
        <p class="filme-sinopse">${escapeHtml(filme.sinopse || 'Sinopse não disponível.')}</p>
        <button class="btn btn-dourado btn-ver-sessoes" onclick="selecionarFilme(${filme.id})">Ver Sessões</button>
      </div>
    </div>
  `;

  return card;
}

function selecionarFilme(filmeId) {
  sessionStorage.setItem('filmeId', filmeId);
  window.location.href = 'sessoes.html';
}
