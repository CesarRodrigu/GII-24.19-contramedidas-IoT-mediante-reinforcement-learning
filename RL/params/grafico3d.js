// Función de reward (recompensa)
function reward(descartados, ocu_actual, action, ocu_ant, coeficientes) {
	let { c, c2, c3, c4 } = coeficientes; // Desempaquetando objeto
	let reward = 0.0;
	console.log("coeficientes", coeficientes);
	if (descartados > 0) {
		if (action === 1) {
			reward -= descartados ** 2 * c;
		} else {
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
function generarDatosSuperficie(x, y, coeficientes, accion) {
	let recompensas = []; // Matriz de recompensas

	let minZ = Infinity;
	let maxZ = -Infinity;
	x.forEach((d) => {
		let filaZ = [];
		y.forEach((o) => {
			let recompensa = reward(d, o, accion, o + 0.1, coeficientes);
			filaZ.push(recompensa);

			// Actualizamos el valor mínimo y máximo de Z
			if (recompensa < minZ) minZ = recompensa;
			if (recompensa > maxZ) maxZ = recompensa;
		});
		recompensas.push(filaZ);
	});

	console.log("x", x);
	console.log("y", y);
	console.log("z", recompensas);
	return { recompensas, minZ, maxZ };
}

// Crear el gráfico 3D con las acciones seleccionadas
function crearPlanoSuperficie(x, y, coeficientes, accion) {
	let datos = generarDatosSuperficie(x, y, coeficientes, accion); // Acción "Permitir"
	let trace = {
		x: x,
		y: y,
		z: datos.recompensas,
		type: "surface",
		colorscale: "Viridis",
		cmin: datos.minZ, // Ajuste dinámico del rango mínimo
		cmax: datos.maxZ, // Ajuste dinámico del rango máximo
		opacity: 0.7,
		colorbar: {
			title: "Recompensa", // Título de la barra de color
			tickvals: [datos.minZ, datos.maxZ],
			ticktext: [`${datos.minZ.toFixed(2)}`, `${datos.maxZ.toFixed(2)}`],
		},
		name: accion === 1 ? "Permitir" : "Denegar",
	};

	return { datos, trace };
}
function crearGrafico3D(precision = 10) {
	let coeficientes = {
		c: parseFloat(document.getElementById("c").value),
		c2: parseFloat(document.getElementById("c2").value),
		c3: parseFloat(document.getElementById("c3").value),
		c4: parseFloat(document.getElementById("c4").value),
	};

	// Obtenemos si se ha seleccionado cada checkbox
	let mostrarPermitir = document.getElementById("checkboxPermitir").checked;
	let mostrarDenegar = document.getElementById("checkboxDenegar").checked;

	let traces = [];
	let minZGlobal = Infinity;
	let maxZGlobal = -Infinity;

	let x = [];
	let y = [];

	for (let d = 0; d <= precision; d++) {
		x.push(d);
	}
	for (let o = 0.0; o <= 1.0; o += 1 / precision) {
		y.push(o);
	}
	// Si el checkbox de "Permitir" está marcado, creamos el gráfico correspondiente
	if (mostrarPermitir) {
		let { datos, trace } = crearPlanoSuperficie(x, y, coeficientes, 1);

		// Actualizamos el minZ y maxZ global
		if (datos.minZ < minZGlobal) minZGlobal = datos.minZ;
		if (datos.maxZ > maxZGlobal) maxZGlobal = datos.maxZ;
		traces.push(trace);
	}

	// Si el checkbox de "Denegar" está marcado, creamos el gráfico correspondiente
	if (mostrarDenegar) {
		let { datos, trace } = crearPlanoSuperficie(x, y, coeficientes, 0);
		if (datos.minZ < minZGlobal) minZGlobal = datos.minZ;
		if (datos.maxZ > maxZGlobal) maxZGlobal = datos.maxZ;
		traces.push(trace);
	}

	// Si no se selecciona ningún checkbox, mostramos un mensaje o mantenemos el gráfico vacío

	// Aplicamos el rango global de minZ y maxZ a ambos gráficos
	traces.forEach((trace) => {
		trace.cmin = minZGlobal;
		trace.cmax = maxZGlobal;
		trace.colorbar.tickvals = [minZGlobal, maxZGlobal];
		trace.colorbar.ticktext = [
			`${minZGlobal.toFixed(2)}`,
			`${maxZGlobal.toFixed(2)}`,
		];
	});

	if (traces.length === 0) {
		traces = [
			{
				x: x,
				y: y,
				z: [],
				type: "surface",
				marker: { size: 12 },
			},
		];
	}

	let layout = {
		title: {
			text: "Reward en función de Descartados y Ocupación",
			font: {
				size: 20,
			},
		},
		scene: {
			xaxis: { title: { text: "Descartados" } },
			yaxis: { title: { text: "Ocupación" } },
			zaxis: { title: { text: "Recompensa" } },
		},
	};

	Plotly.newPlot("grafico3d", traces, layout);
}
// Función de debounce para retrasar la ejecución de la actualización
function debounce(func, wait) {
	let timeout;
	return function () {
		const context = this;
		const args = arguments;
		clearTimeout(timeout);
		timeout = setTimeout(() => func.apply(context, args), wait);
	};
}

// Función para actualizar el gráfico al cambiar los sliders o checkboxes
function actualizarGrafico() {
	actualizarSliders();
	// Volver a crear el gráfico
    const precision = parseInt(document.getElementById("precision").value, 10)-1;
	crearGrafico3D(precision);
	console.log("actualizarGrafico() ejecutada");
}
function actualizarSliders() {
	const ids = ["precision","c", "c2", "c3", "c4"];

	for (let id of ids) {
		document.getElementById(`val-${id}`).textContent =
			document.getElementById(id).value;
	}
}
const actualizarGraficoDebounced = debounce(() => {
	actualizarSliders(); // Actualiza los sliders antes de crear el gráfico
	actualizarGrafico();
}, 100); // Tiempo en milisegundos

document.addEventListener("DOMContentLoaded", () => {
    actualizarSliders();
	// Eventos para los sliders
	document.querySelectorAll("input[type=range]").forEach((slider) => {
		slider.addEventListener("input", actualizarGraficoDebounced);
	});

	// Eventos para los checkboxes
	document
		.getElementById("checkboxPermitir")
		.addEventListener("change", actualizarGrafico);
	document
		.getElementById("checkboxDenegar")
		.addEventListener("change", actualizarGrafico);

	// Inicialización del gráfico al cargar la página
	// Crear el gráfico con los valores predeterminados
	crearGrafico3D();
});
