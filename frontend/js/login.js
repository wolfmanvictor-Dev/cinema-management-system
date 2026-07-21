/**
 * CineVerse — Login do administrador.
 */

let elementos = {};

document.addEventListener('DOMContentLoaded', () => {
  elementos = {
    formulario: document.getElementById('form-login'),
    usuario: document.getElementById('usuario'),
    senha: document.getElementById('senha'),
    erro: document.getElementById('erro-login'),
    botao: document.querySelector('.btn-login')
  };

  elementos.formulario.addEventListener('submit', (evento) => {
    evento.preventDefault();
    fazerLogin();
  });
});

async function fazerLogin() {
  const usuario = elementos.usuario.value.trim();
  const senha = elementos.senha.value.trim();

  ocultarErro();

  if (!usuario || !senha) {
    exibirErro('Preencha o login e a senha.');
    return;
  }

  definirEstadoBotao(true);

  try {
    const dados = await apiFetch(`${API_URL}/admin/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ usuario, senha })
    });

    sessionStorage.setItem('adminToken', dados.token);
    sessionStorage.setItem('adminNome', dados.nome || 'Administrador');

    window.location.href = 'admin.html';
  } catch (erro) {
    exibirErro(erro.message);
    definirEstadoBotao(false);
  }
}

function exibirErro(mensagem) {
  elementos.erro.textContent = mensagem;
  elementos.erro.hidden = false;
}

function ocultarErro() {
  elementos.erro.hidden = true;
}

function definirEstadoBotao(carregando) {
  elementos.botao.disabled = carregando;
  elementos.botao.textContent = carregando ? 'Entrando...' : 'Entrar';
}
