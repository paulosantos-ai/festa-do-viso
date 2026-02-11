// Sistema de Relatórios com API
const API_BASE = 'api/';

class SistemaRelatorios {
    constructor() {
        this.init();
    }

    async init() {
        await this.carregarVencedores();
    }

    async carregarVencedores() {
        try {
            const response = await fetch(`${API_BASE}vencedores.php`);
            const data = await response.json();

            if (!data.success) {
                console.error('Erro ao carregar vencedores:', data.error);
                return;
            }

            const vencedores = data.vencedores;

            if (vencedores.length === 0) {
                document.getElementById('semVencedores').style.display = 'block';
                document.getElementById('listaVencedores').style.display = 'none';
                return;
            }

            document.getElementById('semVencedores').style.display = 'none';
            document.getElementById('listaVencedores').style.display = 'block';

            const lista = document.getElementById('listaVencedores');
            lista.innerHTML = '';

            vencedores.forEach(v => {
                const card = document.createElement('div');
                card.className = 'vencedor-card';

                const dataSorteio = new Date(v.data_sorteio);
                const dataFormatada = dataSorteio.toLocaleDateString('pt-PT', {
                    weekday: 'long',
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric'
                });

                card.innerHTML = `
                    <div class="vencedor-header">
                        <h3>🎊 ${v.folha_nome}</h3>
                        <span class="data-sorteio">${dataFormatada}</span>
                    </div>
                    <div class="vencedor-body">
                        <div class="numero-vencedor">
                            <span class="label">Número Vencedor</span>
                            <span class="numero">${v.numero_vencedor}</span>
                        </div>
                        <div class="info-vencedor">
                            <p><strong>Vencedor:</strong> ${v.vencedor_nome}</p>
                            <p><strong>Contacto:</strong> ${v.vencedor_contacto}</p>
                        </div>
                    </div>
                    <div class="vencedor-footer">
                        <small>Registado em ${new Date(v.data_registo).toLocaleString('pt-PT')}</small>
                    </div>
                `;

                lista.appendChild(card);
            });

        } catch (error) {
            console.error('Erro ao carregar vencedores:', error);
            alert('Erro ao carregar vencedores');
        }
    }
}

// Inicializar quando a página carregar
document.addEventListener('DOMContentLoaded', () => {
    new SistemaRelatorios();
});
