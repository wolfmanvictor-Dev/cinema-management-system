/**
 * CineVerse — Seleção de assentos de uma sessão.
 */

let assentosSelecionados = [];
let precoIngresso = 0;

document.addEventListener('DOMContentLoaded', () => {
  const sessaoId = sessionStorage.getItem('sessaoId');

  if (!sessaoId) {
    window.location.href = 'index.html';
    return;
  }

  carregarSessaoEAssentos(sessaoId);
});

async function carregarSessaoEAssentos(sessaoId) {
  try {
    const sessao = await apiFetch(`${API_URL}/sessoes/${sessaoId}`);
    precoIngresso = sessao.preco;

    document.getElementById('filme-titulo').textContent = sessao.filme.titulo;
    document.getElementById('filme-sessao').textContent =
      `${sessao.sala.nome} · ${nomeDiaSemana(sessao.data)} ${formatarDataBr(sessao.data)} às ${sessao.horario}`;
    document.getElementById('valor-unitario').textContent = formatarMoeda(precoIngresso);
    document.title = `${sessao.filme.titulo} — CineVerse`;

    await carregarAssentos(sessaoId);
  } catch (erro) {
    document.getElementById('filme-titulo').textContent = 'Sessão não encontrada';
    document.getElementById('assentos-container').innerHTML =
      `<div class="loading-msg">${escapeHtml(erro.message)}</div>`;
  }
}

async function carregarAssentos(sessaoId) {
  const container = document.getElementById('assentos-container');

  try {
    const assentos = await apiFetch(`${API_URL}/assentos/sessao/${sessaoId}`);
    container.innerHTML = '';

    const assentosPorFileira = agruparPorFileira(assentos);

    Object.entries(assentosPorFileira).forEach(([letra, assentosFileira]) => {
      container.appendChild(criarFileira(letra, assentosFileira));
    });
  } catch (erro) {
    container.innerHTML = `<div class="loading-msg">${escapeHtml(erro.message)}</div>`;
  }
}

function criarFileira(letra, assentosFileira) {
  const fileira = document.createElement('div');
  fileira.className = 'fileira';
  fileira.innerHTML = `<span class="fileira-label">${letra}</span>`;

  const metade = Math.ceil(assentosFileira.length / 2);

  assentosFileira.forEach((assento, indice) => {
    if (indice === metade) {
      const corredor = document.createElement('div');
      corredor.className = 'corredor';
      fileira.appendChild(corredor);
    }
    fileira.appendChild(criarBotaoAssento(letra, assento));
  });

  return fileira;
}

function criarBotaoAssento(letra, assento) {
  const botao = document.createElement('button');
  const classeVip = assento.tipo === 'VIP' ? ' vip' : '';
  botao.className = `assento ${assento.disponivel ? 'livre' + classeVip : 'ocupado'}`;
  botao.textContent = assento.numero;
  botao.dataset.id = assento.id;
  botao.title = `Assento ${letra}${assento.numero}${assento.tipo === 'VIP' ? ' (VIP)' : ''}`;

  if (assento.disponivel) {
    botao.addEventListener('click', () => alternarAssento(botao, assento.id));
  }

  return botao;
}

function agruparPorFileira(assentos) {
  return assentos.reduce((grupos, assento) => {
    const letra = assento.fileira || 'A';
    if (!grupos[letra]) grupos[letra] = [];
    grupos[letra].push(assento);
    return grupos;
  }, {});
}

function alternarAssento(botao, assentoId) {
  const indice = assentosSelecionados.indexOf(assentoId);
  const eraVip = botao.classList.contains('vip');

  if (indice === -1) {
    assentosSelecionados.push(assentoId);
    botao.classList.remove('livre', 'vip');
    botao.classList.add('selecionado');
    botao.dataset.eraVip = eraVip ? '1' : '0';
  } else {
    assentosSelecionados.splice(indice, 1);
    botao.classList.remove('selecionado');
    botao.classList.add('livre');
    if (botao.dataset.eraVip === '1') botao.classList.add('vip');
  }

  atualizarResumo();
}

function atualizarResumo() {
  const quantidade = assentosSelecionados.length;
  const total = quantidade * precoIngresso;

  document.getElementById('qtd-selecionados').textContent = quantidade;
  document.getElementById('total-preco').textContent = formatarMoeda(total);
  document.getElementById('btn-continuar').disabled = quantidade === 0;
}

function irParaPagamento() {
  if (assentosSelecionados.length === 0) return;

  sessionStorage.setItem('assentosSelecionados', JSON.stringify(assentosSelecionados));
  sessionStorage.setItem('totalPagamento', (assentosSelecionados.length * precoIngresso).toFixed(2));

  window.location.href = 'pagamento.html';
}
