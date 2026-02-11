// Gestão do Sistema de Sorteio com API
const API_BASE = 'api/';

class SistemaSorteio {
    constructor() {
        this.folhaAtual = 1;
        this.numeroSelecionado = null;
        this.init();
    }

    async init() {
        await this.carregarFolhas();
        this.gerarNumeros();
        this.setupEventListeners();
        await this.carregarDadosFolha();
    }

    async carregarFolhas() {
        try {
            const response = await fetch(`${API_BASE}folhas.php`);
            const data = await response.json();

            if (!data.success) {
                console.error('Erro ao carregar folhas:', data.error);
                return;
            }

            const folhas = data.folhas;
            const select = document.getElementById('folhaSelect');
            select.innerHTML = '';

            folhas.forEach(folha => {
                const option = document.createElement('option');
                option.value = folha.id;
                option.textContent = `Folha ${folha.id} - ${folha.nome}`;
                select.appendChild(option);
            });

        } catch (error) {
            console.error('Erro ao carregar folhas:', error);
            alert('Erro ao carregar folhas de sorteio');
        }
    }

    gerarNumeros() {
        const grid = document.getElementById('numerosGrid');
        grid.innerHTML = '';

        for (let i = 1; i <= 49; i++) {
            const numeroDiv = document.createElement('div');
            numeroDiv.className = 'numero-box';
            numeroDiv.dataset.numero = i;
            numeroDiv.textContent = i;
            numeroDiv.addEventListener('click', () => this.selecionarNumero(i));
            grid.appendChild(numeroDiv);
        }
    }

    async carregarDadosFolha() {
        const folhaId = document.getElementById('folhaSelect').value;

        try {
            const response = await fetch(`${API_BASE}registos.php?folha_id=${folhaId}`);
            const data = await response.json();

            if (!data.success) {
                console.error('Erro ao carregar registos:', data.error);
                return;
            }

            const registos = data.registos;

            // Atualizar status da folha
            const ocupados = Object.keys(registos).length;
            document.getElementById('folhaStatus').textContent = `${ocupados}/49 números ocupados`;

            // Atualizar visualização dos números
            document.querySelectorAll('.numero-box').forEach(box => {
                const numero = box.dataset.numero;
                box.className = 'numero-box';

                if (registos[numero]) {
                    box.classList.add('ocupado');
                    box.title = `Ocupado por ${registos[numero].nome}`;
                } else {
                    box.classList.add('disponivel');
                }
            });

            this.registos = registos;

        } catch (error) {
            console.error('Erro ao carregar dados da folha:', error);
            alert('Erro ao carregar dados da folha');
        }
    }

    selecionarNumero(numero) {
        if (this.registos[numero]) {
            // Número ocupado - mostrar informações
            this.mostrarInfoNumero(numero, this.registos[numero]);
        } else {
            // Número disponível - abrir formulário de registo
            this.abrirFormularioRegisto(numero);
        }
    }

    abrirFormularioRegisto(numero) {
        this.numeroSelecionado = numero;
        document.getElementById('numeroSelecionado').textContent = numero;
        document.getElementById('registoModal').style.display = 'block';
        document.getElementById('nome').value = '';
        document.getElementById('contacto').value = '';
    }

    mostrarInfoNumero(numero, info) {
        document.getElementById('numeroVisualizado').textContent = numero;
        document.getElementById('infoParticipante').innerHTML = `
            <p><strong>Nome:</strong> ${info.nome}</p>
            <p><strong>Contacto:</strong> ${info.contacto}</p>
            <p><strong>Data de Registo:</strong> ${new Date(info.dataRegisto).toLocaleString('pt-PT')}</p>
        `;
        document.getElementById('visualizarModal').style.display = 'block';
    }

    async registarNumero(nome, contacto) {
        const folhaId = document.getElementById('folhaSelect').value;

        try {
            const response = await fetch(`${API_BASE}registos.php`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    folha_id: parseInt(folhaId),
                    numero: this.numeroSelecionado,
                    nome: nome,
                    contacto: contacto
                })
            });

            const data = await response.json();

            if (!data.success) {
                if (response.status === 409) {
                    alert('Este número já foi registado!');
                } else {
                    alert('Erro: ' + data.error);
                }
                return false;
            }

            await this.carregarDadosFolha();
            this.fecharModal('registoModal');

            alert(`Número ${this.numeroSelecionado} registado com sucesso!\nBoa sorte no sorteio de sexta-feira!`);
            return true;

        } catch (error) {
            console.error('Erro ao registar número:', error);
            alert('Erro ao registar número. Tente novamente.');
            return false;
        }
    }

    fecharModal(modalId) {
        document.getElementById(modalId).style.display = 'none';
    }

    setupEventListeners() {
        // Mudança de folha
        document.getElementById('folhaSelect').addEventListener('change', async () => {
            await this.carregarDadosFolha();
        });

        // Formulário de registo
        document.getElementById('registoForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const nome = document.getElementById('nome').value;
            const contacto = document.getElementById('contacto').value;
            await this.registarNumero(nome, contacto);
        });

        // Botões de fechar modais
        document.querySelectorAll('.close').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const modal = e.target.closest('.modal');
                modal.style.display = 'none';
            });
        });

        // Botão cancelar
        document.getElementById('cancelarBtn').addEventListener('click', () => {
            this.fecharModal('registoModal');
        });

        // Fechar modal ao clicar fora
        window.addEventListener('click', (e) => {
            if (e.target.classList.contains('modal')) {
                e.target.style.display = 'none';
            }
        });
    }
}

// Inicializar quando a página carregar
document.addEventListener('DOMContentLoaded', () => {
    new SistemaSorteio();
});
