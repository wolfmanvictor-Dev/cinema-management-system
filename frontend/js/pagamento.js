/**
 * CineVerse — Finalização da compra (pagamento).
 */

let metodoPagamento = 'cartao';
let sessaoAtual = null;
let assentosCodigos = [];

document.addEventListener('DOMContentLoaded', () => {
  const sessaoId = sessionStorage.getItem('sessaoId');
  const assentos = sessionStorage.getItem('assentosSelecionados');
  const total = sessionStorage.getItem('totalPagamento');

  if (!sessaoId || !assentos || !total) {
    window.location.href = 'index.html';
    return;
  }

  preencherResumo(sessaoId, JSON.parse(assentos), parseFloat(total));
});

async function preencherResumo(sessaoId, idsAssentos, total) {
  try {
    sessaoAtual = await apiFetch(`${API_URL}/sessoes/${sessaoId}`);

    document.getElementById('resumo-filme').textContent = sessaoAtual.filme.titulo;
    document.getElementById('resumo-sessao').textContent =
      `${sessaoAtual.sala.nome} · ${formatarDataBr(sessaoAtual.data)} às ${sessaoAtual.horario}`;
    document.getElementById('resumo-valor-unitario').textContent = formatarMoeda(sessaoAtual.preco);

    preencherParcelas(total);
    await preencherAssentos(sessaoId, idsAssentos);

    document.getElementById('resumo-qtd').textContent = `${idsAssentos.length} ingresso(s)`;
    document.getElementById('resumo-total').textContent = formatarMoeda(total);
  } catch (erro) {
    toast(erro.message, 'erro');
  }
}

function preencherParcelas(total) {
  const select = document.getElementById('parcelas');
  select.innerHTML = [1, 2, 3].map(parcelas => {
    const valorParcela = total / parcelas;
    return `<option value="${parcelas}">${parcelas}x de ${formatarMoeda(valorParcela)} (sem juros)</option>`;
  }).join('');
}

async function preencherAssentos(sessaoId, idsAssentos) {
  try {
    const todosAssentos = await apiFetch(`${API_URL}/assentos/sessao/${sessaoId}`);
    const selecionados = todosAssentos.filter(a => idsAssentos.includes(a.id));
    assentosCodigos = selecionados.map(a => `${a.fileira}${a.numero}`);
    document.getElementById('resumo-assentos').textContent = assentosCodigos.join(', ') || '—';
  } catch (erro) {
    document.getElementById('resumo-assentos').textContent = `${idsAssentos.length} selecionado(s)`;
  }
}

function selecionarMetodo(metodo, botao) {
  metodoPagamento = metodo;

  document.querySelectorAll('.metodo-tab').forEach(aba => aba.classList.remove('active'));
  botao.classList.add('active');

  document.querySelectorAll('.metodo-form').forEach(formulario => formulario.style.display = 'none');
  document.getElementById(`form-${metodo}`).style.display = 'block';
}

async function finalizarCompra() {
  limparErros();

  const comprador = obterDadosComprador();

  if (!validarComprador(comprador)) return;
  if (metodoPagamento === 'cartao' && !validarCartao()) {
    toast('Preencha todos os dados do cartão.', 'erro');
    return;
  }

  const botao = document.querySelector('.btn-finalizar');
  botao.disabled = true;
  botao.textContent = 'Processando...';

  const compra = {
    sessaoId: parseInt(sessionStorage.getItem('sessaoId'), 10),
    assentoIds: JSON.parse(sessionStorage.getItem('assentosSelecionados')),
    metodoPagamento,
    comprador
  };

  try {
    const resultado = await apiFetch(`${API_URL}/compras`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(compra)
    });

    exibirConfirmacao(resultado);
    limparSessaoCompra();
  } catch (erro) {
    toast(erro.message, 'erro', 6000);
    botao.disabled = false;
    botao.textContent = 'Confirmar Pagamento';
  }
}

function obterDadosComprador() {
  return {
    nome: document.getElementById('nome-comprador').value.trim(),
    email: document.getElementById('email-comprador').value.trim(),
    cpf: document.getElementById('cpf-comprador').value.trim()
  };
}

function validarComprador(comprador) {
  if (!comprador.nome || !comprador.email || !comprador.cpf) {
    toast('Preencha todos os dados do comprador.', 'erro');
    return false;
  }
  const emailValido = /^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(comprador.email);
  if (!emailValido) {
    toast('Informe um e-mail válido.', 'erro');
    return false;
  }
  const cpfNumeros = comprador.cpf.replace(/\D/g, '');
  if (cpfNumeros.length !== 11) {
    toast('Informe um CPF válido (11 dígitos).', 'erro');
    return false;
  }
  return true;
}

function validarCartao() {
  const campos = ['num-cartao', 'nome-cartao', 'validade', 'cvv'];
  return campos.every(id => document.getElementById(id).value.trim() !== '');
}

function limparErros() {
  document.querySelectorAll('.campo-erro').forEach(el => el.classList.remove('campo-erro'));
}

function exibirConfirmacao(compra) {
  document.getElementById('ticket-filme').textContent = sessaoAtual.filme.titulo;
  document.getElementById('ticket-sessao').textContent =
    `${sessaoAtual.sala.nome} · ${formatarDataBr(sessaoAtual.data)} às ${sessaoAtual.horario}`;
  document.getElementById('ticket-assentos').textContent = assentosCodigos.join(', ');
  document.getElementById('ticket-total').textContent = formatarMoeda(compra.total);
  document.getElementById('ticket-codigo').textContent = `Código: ${compra.codigo}`;
  document.getElementById('modal-sucesso').style.display = 'flex';
}

function limparSessaoCompra() {
  sessionStorage.removeItem('sessaoId');
  sessionStorage.removeItem('filmeId');
  sessionStorage.removeItem('assentosSelecionados');
  sessionStorage.removeItem('totalPagamento');
}

function voltarInicio() {
  window.location.href = 'index.html';
}

function formatarCartao(input) {
  const numeros = input.value.replace(/\D/g, '').substring(0, 16);
  input.value = numeros.replace(/(.{4})/g, '$1 ').trim();
}

function formatarValidade(input) {
  let valor = input.value.replace(/\D/g, '').substring(0, 4);
  if (valor.length >= 3) valor = `${valor.substring(0, 2)}/${valor.substring(2)}`;
  input.value = valor;
}

function formatarCPF(input) {
  let valor = input.value.replace(/\D/g, '').substring(0, 11);
  if (valor.length > 9) valor = `${valor.substring(0, 3)}.${valor.substring(3, 6)}.${valor.substring(6, 9)}-${valor.substring(9)}`;
  else if (valor.length > 6) valor = `${valor.substring(0, 3)}.${valor.substring(3, 6)}.${valor.substring(6)}`;
  else if (valor.length > 3) valor = `${valor.substring(0, 3)}.${valor.substring(3)}`;
  input.value = valor;
}
