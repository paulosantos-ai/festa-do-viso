// Sistema de Administração com API
const API_BASE = 'api/';

class SistemaAdmin {
    constructor() {
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.verificarSessao();
    }

    setupEventListeners() {
        // Login
        document.getElementById('loginForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            await this.login();
        });

        // Logout
        document.getElementById('logoutBtn').addEventListener('click', async () => {
            await this.logout();
        });

        // Nova Folha
        document.getElementById('addFolhaBtn').addEventListener('click', () => {
            document.getElementById('novaFolhaModal').style.display = 'block';
        });

        document.getElementById('novaFolhaForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            await this.criarNovaFolha();
        });

        document.getElementById('cancelarFolhaBtn').addEventListener('click', () => {
            document.getElementById('novaFolhaModal').style.display = 'none';
        });

        // Registar Vencedor
        document.getElementById('vencedorForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            await this.registarVencedor();
        });

        // Fechar modais
        document.querySelectorAll('.close').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const modal = e.target.closest('.modal');
                modal.style.display = 'none';
            });
        });

        window.addEventListener('click', (e) => {
            if (e.target.classList.contains('modal')) {
                e.target.style.display = 'none';
            }
        });
    }

    async login() {
        const password = document.getElementById('password').value;

        try {
            const response = await fetch(`${API_BASE}auth.php`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    action: 'login',
                    password: password
                })
            });

            const data = await response.json();

            if (data.success) {
                this.mostrarPainelAdmin();
            } else {
                alert('Palavra-passe incorreta!');
            }
        } catch (error) {
            console.error('Erro no login:', error);
            alert('Erro ao fazer login');
        }
    }

    async logout() {
        try {
            await fetch(`${API_BASE}auth.php`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    action: 'logout'
                })
            });

            this.mostrarLogin();
        } catch (error) {
            console.error('Erro no logout:', error);
        }
    }

    async verificarSessao() {
        try {
            const response = await fetch(`${API_BASE}auth.php`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    action: 'check'
                })
            });

            const data = await response.json();

            if (data.logado) {
                this.mostrarPainelAdmin();
            }
        } catch (error) {
            console.error('Erro ao verificar sessão:', error);
        }
    }

    mostrarPainelAdmin() {
        document.getElementById('loginArea').style.display = 'none';
        document.getElementById('adminArea').style.display = 'block';
        this.carregarDadosAdmin();
    }

    mostrarLogin() {
        document.getElementById('loginArea').style.display = 'block';
        document.getElementById('adminArea').style.display = 'none';
        document.getElementById('password').value = '';
    }

    async carregarDadosAdmin() {
        await Promise.all([
            this.carregarListaFolhas(),
            this.carregarSelectFolhas(),
            this.carregarEstatisticas()
        ]);
    }

    async carregarListaFolhas() {
        try {
            const response = await fetch(`${API_BASE}folhas.php`);
            const data = await response.json();

            if (!data.success) {
                console.error('Erro ao carregar folhas:', data.error);
                return;
            }

            const folhas = data.folhas;
            const lista = document.getElementById('folhasList');
            lista.innerHTML = '';

            folhas.forEach(folha => {
                const ocupados = folha.numeros_ocupados || 0;

                const folhaCard = document.createElement('div');
                folhaCard.className = 'folha-card';
                folhaCard.innerHTML = `
                    <div class="folha-info">
                        <h4>${folha.nome}</h4>
                        <p>ID: ${folha.id}</p>
                        <p>Números ocupados: ${ocupados}/49</p>
                        <p>Criada em: ${new Date(folha.data_criacao).toLocaleDateString('pt-PT')}</p>
                    </div>
                    <div class="folha-actions">
                        <button class="btn-secondary btn-small" onclick="admin.verDetalhesFolha(${folha.id})">Ver Detalhes</button>
                        ${folhas.length > 1 ? `<button class="btn-danger btn-small" onclick="admin.eliminarFolha(${folha.id})">Eliminar</button>` : ''}
                    </div>
                `;
                lista.appendChild(folhaCard);
            });
        } catch (error) {
            console.error('Erro ao carregar lista de folhas:', error);
        }
    }

    async carregarSelectFolhas() {
        try {
            const response = await fetch(`${API_BASE}folhas.php`);
            const data = await response.json();

            if (!data.success) return;

            const folhas = data.folhas;
            const select = document.getElementById('folhaVencedor');
            select.innerHTML = '';

            folhas.forEach(folha => {
                const option = document.createElement('option');
                option.value = folha.id;
                option.textContent = `${folha.nome} (ID: ${folha.id})`;
                select.appendChild(option);
            });
        } catch (error) {
            console.error('Erro ao carregar select de folhas:', error);
        }
    }

    async carregarEstatisticas() {
        try {
            const [folhasRes, vencedoresRes] = await Promise.all([
                fetch(`${API_BASE}folhas.php`),
                fetch(`${API_BASE}vencedores.php`)
            ]);

            const folhasData = await folhasRes.json();
            const vencedoresData = await vencedoresRes.json();

            if (!folhasData.success || !vencedoresData.success) return;

            const folhas = folhasData.folhas;
            const vencedores = vencedoresData.vencedores;

            let totalNumeros = 0;
            folhas.forEach(folha => {
                totalNumeros += parseInt(folha.numeros_ocupados) || 0;
            });

            const stats = document.getElementById('estatisticas');
            stats.innerHTML = `
                <div class="stat-card">
                    <h4>${folhas.length}</h4>
                    <p>Folhas Ativas</p>
                </div>
                <div class="stat-card">
                    <h4>${totalNumeros}</h4>
                    <p>Números Vendidos</p>
                </div>
                <div class="stat-card">
                    <h4>${vencedores.length}</h4>
                    <p>Sorteios Realizados</p>
                </div>
                <div class="stat-card">
                    <h4>${(folhas.length * 49) - totalNumeros}</h4>
                    <p>Números Disponíveis</p>
                </div>
            `;
        } catch (error) {
            console.error('Erro ao carregar estatísticas:', error);
        }
    }

    async criarNovaFolha() {
        const nome = document.getElementById('nomeFolha').value;

        try {
            const response = await fetch(`${API_BASE}folhas.php`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ nome: nome })
            });

            const data = await response.json();

            if (data.success) {
                document.getElementById('novaFolhaModal').style.display = 'none';
                document.getElementById('nomeFolha').value = '';
                alert(`Folha "${nome}" criada com sucesso!`);
                this.carregarDadosAdmin();
            } else {
                alert('Erro: ' + data.error);
            }
        } catch (error) {
            console.error('Erro ao criar folha:', error);
            alert('Erro ao criar folha');
        }
    }

    async eliminarFolha(id) {
        if (!confirm('Tem a certeza que deseja eliminar esta folha? Esta ação não pode ser revertida.')) {
            return;
        }

        try {
            const response = await fetch(`${API_BASE}folhas.php`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ id: id })
            });

            const data = await response.json();

            if (data.success) {
                alert('Folha eliminada com sucesso!');
                this.carregarDadosAdmin();
            } else {
                alert('Erro: ' + data.error);
            }
        } catch (error) {
            console.error('Erro ao eliminar folha:', error);
            alert('Erro ao eliminar folha');
        }
    }

    async verDetalhesFolha(id) {
        try {
            const response = await fetch(`${API_BASE}registos.php?folha_id=${id}`);
            const data = await response.json();

            if (!data.success) {
                alert('Erro ao carregar detalhes');
                return;
            }

            const registos = data.registos;
            const folhasResponse = await fetch(`${API_BASE}folhas.php`);
            const folhasData = await folhasResponse.json();
            const folha = folhasData.folhas.find(f => f.id === id);

            let detalhes = `Detalhes da ${folha.nome}:\n\n`;
            detalhes += `Total de números ocupados: ${Object.keys(registos).length}/49\n\n`;
            detalhes += `Números registados:\n`;

            for (const [numero, info] of Object.entries(registos)) {
                detalhes += `Nº ${numero}: ${info.nome} (${info.contacto})\n`;
            }

            alert(detalhes);
        } catch (error) {
            console.error('Erro ao ver detalhes:', error);
            alert('Erro ao carregar detalhes');
        }
    }

    async registarVencedor() {
        const folhaId = parseInt(document.getElementById('folhaVencedor').value);
        const dataSorteio = document.getElementById('dataSorteio').value;
        const numeroVencedor = parseInt(document.getElementById('numeroVencedor').value);

        try {
            const response = await fetch(`${API_BASE}vencedores.php`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    folha_id: folhaId,
                    data_sorteio: dataSorteio,
                    numero_vencedor: numeroVencedor
                })
            });

            const data = await response.json();

            if (data.success) {
                if (data.sem_vencedor) {
                    alert(data.message);
                } else {
                    const v = data.vencedor;
                    alert(`Vencedor registado com sucesso!\n\nNúmero: ${v.numero}\nVencedor: ${v.nome}\nContacto: ${v.contacto}`);
                }
                document.getElementById('vencedorForm').reset();
                this.carregarEstatisticas();
            } else {
                alert('Erro: ' + data.error);
            }
        } catch (error) {
            console.error('Erro ao registar vencedor:', error);
            alert('Erro ao registar vencedor');
        }
    }
}

// Instância global para permitir chamadas de onclick
let admin;

document.addEventListener('DOMContentLoaded', () => {
    admin = new SistemaAdmin();
});
