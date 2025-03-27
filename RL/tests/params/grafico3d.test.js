const fs = require('fs');
const path = require('path');
const { reward } = require('../../params/grafico3d.js'); // Verificar que reward esté correctamente exportada

beforeEach(() => {
    // Cargar el contenido de Params.html en el DOM
    const html = fs.readFileSync(
        path.resolve(__dirname, '../../params/Params.html'),
        'utf8'
    );
    document.body.innerHTML = html;

    // Simular la ejecución del script después de cargar el DOM
    require('../../params/grafico3d.js');
});

test('sumar 1 + 2 es igual a 3', () => {
    expect(typeof reward).toBe('function'); // Asegurarse de que reward es una función
    const result = reward(1, 2, 3, 1, 2, 3, 4, 5);
    expect(result).not.toBe(0); // Asegurarse de que el resultado no sea 0
});