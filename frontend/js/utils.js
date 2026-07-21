/**
 * CineVerse — utils.js
 * Funções reaproveitadas por todas as telas: notificações (toast),
 * formatação de moeda/data, e um helper de fetch com tratamento de erro
 * padronizado. Deve ser incluído em toda página, antes do script da página.
 */

const API_URL = 'http://localhost:8080/api';

// ---------------------------------------------------------------------------
// Toasts — substituem o uso de alert()/confirm() nativos do navegador
// ---------------------------------------------------------------------------

function garantirContainerToast() {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    document.body.appendChild(container);
  }
  return container;
}

function toast(mensagem, tipo = 'info', duracaoMs = 4200) {
  const container = garantirContainerToast();

  const elemento = document.createElement('div');
  elemento.className = `toast ${tipo}`;
  elemento.textContent = mensagem;
  container.appendChild(elemento);

  setTimeout(() => {
    elemento.classList.add('saindo');
    setTimeout(() => elemento.remove(), 200);
  }, duracaoMs);
}

// ---------------------------------------------------------------------------
// Formatação
// ---------------------------------------------------------------------------

function formatarMoeda(valor) {
  const numero = typeof valor === 'number' ? valor : parseFloat(valor || 0);
  return `R$ ${numero.toFixed(2).replace('.', ',')}`;
}

function formatarDataBr(isoDate) {
  if (!isoDate) return '—';
  const [ano, mes, dia] = isoDate.split('-');
  return `${dia}/${mes}/${ano}`;
}

function formatarDataHoraBr(isoString) {
  if (!isoString) return '—';
  const data = new Date(isoString);
  const dataFormatada = data.toLocaleDateString('pt-BR');
  const horaFormatada = data.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
  return `${dataFormatada} ${horaFormatada}`;
}

function nomeDiaSemana(isoDate) {
  const dias = ['Domingo', 'Segunda-feira', 'Terça-feira', 'Quarta-feira', 'Quinta-feira', 'Sexta-feira', 'Sábado'];
  const data = new Date(`${isoDate}T00:00:00`);
  return dias[data.getDay()];
}

function escapeHtml(texto) {
  const div = document.createElement('div');
  div.textContent = texto ?? '';
  return div.innerHTML;
}

// ---------------------------------------------------------------------------
// Fetch com tratamento de erro padronizado
// ---------------------------------------------------------------------------

/**
 * Faz uma requisição e já trata erros de rede e de API (formato { erro: "..." }).
 * Retorna os dados em JSON em caso de sucesso, ou lança um Error com mensagem amigável.
 */
async function apiFetch(url, opcoes = {}) {
  let response;
  try {
    response = await fetch(url, opcoes);
  } catch (erro) {
    throw new Error('Não foi possível conectar ao servidor. Verifique se a API está rodando em ' + API_URL);
  }

  let dados = null;
  const temCorpo = response.status !== 204;
  if (temCorpo) {
    dados = await response.json().catch(() => null);
  }

  if (!response.ok) {
    const mensagem = (dados && dados.erro) ? dados.erro : `Erro inesperado (código ${response.status}).`;
    throw new Error(mensagem);
  }

  return dados;
}
