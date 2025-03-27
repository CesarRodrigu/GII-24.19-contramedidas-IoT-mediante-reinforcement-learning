// Función de reward (recompensa)
function reward(descartados, ocu_actual, action, ocu_ant, c, c2, c3, c4) {
    let reward = 0.0;

    if (descartados > 0) {
        if (action === 1) {  // PERMITIR
            reward -= (descartados ** 2) * c;
        } else {  // DENEGAR
            reward -= descartados * c2;
        }
        let mejora = ocu_ant - ocu_actual;
        reward += mejora * c3;
    } else {
        reward += (1.0 - ocu_actual) * c4;
    }

    return reward;
}

// Generar datos para el gráfico de superficie con una acción seleccionada
function generarDatosSuperficie(c, c2, c3, c4, accion) {
    let x = [];  // Valores de descartados
    let y = [];  // Valores de ocupación
    let z = [];  // Matriz de recompensas

    let minZ = Infinity;
    let maxZ = -Infinity;

    for (let d = 0; d <= 10; d++) {
        let filaZ = [];
        for (let o = 0.0; o <= 1.0; o += 0.1) {
            let recompensa = reward(d, o, accion, o + 0.1, c, c2, c3, c4);
            filaZ.push(recompensa);

            // Actualizamos el valor mínimo y máximo de Z
            if (recompensa < minZ) minZ = recompensa;
            if (recompensa > maxZ) maxZ = recompensa;
        }
        x.push(d);
        z.push(filaZ);
    }

    for (let o = 0.0; o <= 1.0; o += 0.1) {
        y.push(o);
    }

    return { x, y, z, minZ, maxZ };
}

// Crear el gráfico 3D con las acciones seleccionadas
function crearGrafico3D() {
    let c = parseFloat(document.getElementById("c").value);
    let c2 = parseFloat(document.getElementById("c2").value);
    let c3 = parseFloat(document.getElementById("c3").value);
    let c4 = parseFloat(document.getElementById("c4").value);

    // Obtenemos si se ha seleccionado cada checkbox
    let mostrarPermitir = document.getElementById("checkboxPermitir").checked;
    let mostrarDenegar = document.getElementById("checkboxDenegar").checked;

    let traces = [];
    let minZGlobal = Infinity;
    let maxZGlobal = -Infinity;

    // Si el checkbox de "Permitir" está marcado, creamos el gráfico correspondiente
    if (mostrarPermitir) {
        let datosPermitir = generarDatosSuperficie(c, c2, c3, c4, 1);  // Acción "Permitir"
        let tracePermitir = {
            x: datosPermitir.x,
            y: datosPermitir.y,
            z: datosPermitir.z,
            type: 'surface',
            colorscale: 'Viridis',
            cmin: datosPermitir.minZ, // Ajuste dinámico del rango mínimo
            cmax: datosPermitir.maxZ, // Ajuste dinámico del rango máximo
            opacity: 0.7,
            colorbar: {
                title: 'Recompensa', // Título de la barra de color
                tickvals: [datosPermitir.minZ, datosPermitir.maxZ],
                ticktext: [`${datosPermitir.minZ.toFixed(2)}`, `${datosPermitir.maxZ.toFixed(2)}`]
            },
            name: 'Permitir'
        };
        traces.push(tracePermitir);

        // Actualizamos el minZ y maxZ global
        if (datosPermitir.minZ < minZGlobal) minZGlobal = datosPermitir.minZ;
        if (datosPermitir.maxZ > maxZGlobal) maxZGlobal = datosPermitir.maxZ;
    }

    // Si el checkbox de "Denegar" está marcado, creamos el gráfico correspondiente
    if (mostrarDenegar) {
        let datosDenegar = generarDatosSuperficie(c, c2, c3, c4, 0);  // Acción "Denegar"
        let traceDenegar = {
            x: datosDenegar.x,
            y: datosDenegar.y,
            z: datosDenegar.z,
            type: 'surface',
            colorscale: 'Cividis',
            cmin: datosDenegar.minZ, // Ajuste dinámico del rango mínimo
            cmax: datosDenegar.maxZ, // Ajuste dinámico del rango máximo
            opacity: 0.7,
            colorbar: {
                title: 'Recompensa', // Título de la barra de color
                tickvals: [datosDenegar.minZ, datosDenegar.maxZ],
                ticktext: [`${datosDenegar.minZ.toFixed(2)}`, `${datosDenegar.maxZ.toFixed(2)}`]
            },
            name: 'Denegar'
        };
        traces.push(traceDenegar);

        // Actualizamos el minZ y maxZ global
        if (datosDenegar.minZ < minZGlobal) minZGlobal = datosDenegar.minZ;
        if (datosDenegar.maxZ > maxZGlobal) maxZGlobal = datosDenegar.maxZ;
    }

    // Si no se selecciona ningún checkbox, mostramos un mensaje o mantenemos el gráfico vacío
    if (traces.length === 0) {
        traces = [{
            x: [],
            y: [],
            z: [],
            type: 'scatter3d',
            mode: 'markers',
            marker: { size: 12 }
        }];
    }

    // Aplicamos el rango global de minZ y maxZ a ambos gráficos
    traces.forEach(trace => {
        trace.cmin = minZGlobal;
        trace.cmax = maxZGlobal;
        trace.colorbar.tickvals = [minZGlobal, maxZGlobal];
        trace.colorbar.ticktext = [`${minZGlobal.toFixed(2)}`, `${maxZGlobal.toFixed(2)}`];
    });

    let layout = {
        title: 'Reward en función de Descartados y Ocupación',
        scene: {
            xaxis: { title: 'Descartados' },
            yaxis: { title: 'Ocupación' },
            zaxis: { title: 'Recompensa' }
        }
    };

    Plotly.newPlot('grafico3d', traces, layout);
}
// Función de debounce para retrasar la ejecución de la actualización
function debounce(func, wait) {
    let timeout;
    return function() {
        const context = this;
        const args = arguments;
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(context, args), wait);
    };
}


// Función para actualizar el gráfico al cambiar los sliders o checkboxes
function actualizarGrafico() {
    document.getElementById("val-c").textContent = document.getElementById("c").value;
    document.getElementById("val-c2").textContent = document.getElementById("c2").value;
    document.getElementById("val-c3").textContent = document.getElementById("c3").value;
    document.getElementById("val-c4").textContent = document.getElementById("c4").value;

    // Volver a crear el gráfico
    crearGrafico3D();
}

// Crear una función de debounce con un retraso de 500 ms
const actualizarGraficoDebounced = debounce(actualizarGrafico, 100);

// Eventos para los sliders
document.querySelectorAll("input[type=range]").forEach(slider => {
    slider.addEventListener("input", actualizarGraficoDebounced);
});

// Eventos para los checkboxes
document.getElementById("checkboxPermitir").addEventListener("change", actualizarGraficoDebounced);
document.getElementById("checkboxDenegar").addEventListener("change", actualizarGraficoDebounced);

// Inicialización del gráfico al cargar la página
document.addEventListener("DOMContentLoaded", () => {
    // Crear el gráfico con los valores predeterminados
    crearGrafico3D();
});
