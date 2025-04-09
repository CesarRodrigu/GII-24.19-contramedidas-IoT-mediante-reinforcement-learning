const Action = Object.freeze({
	PERMITIR: 0,
	DENEGAR: 1,
});
const tamCola = 250;
const vProcesamiento = 5e6 / 8;
const duration_step = 1e-3;

// Función de reward (recompensa)
function reward(descartados, ocu_actual, action, ocu_ant, coeficientes) {
	let { c, c2, c3, c4 } = coeficientes;

	let reward = 0.0;
	if (descartados > 0) {
		if (action === Action.PERMITIR) {
			reward -= descartados ** 2 * c;
		} else {
			reward -= descartados * c2;
		}

		let mejora = ocu_ant - ocu_actual;
		reward += mejora * ocu_actual * c3;
	} else {
		reward += (1.0 - ocu_actual) * c4;
	}

	return reward;
}
function calc_descartados(ocu_actual, paquetes_entrantes, accion) {
	let descartados = 0;
	if (accion == Action.DENEGAR) {
		descartados = paquetes_entrantes * tamCola;
	} else {
		let desc = 0;
		const suma = ocu_actual + paquetes_entrantes;

		if (suma > 1.0) {
			desc = suma - 1.0;
			descartados = desc * tamCola;
		}
	}
	return Math.round(descartados);
}
function reward_funtion(
	ocu_actual,
	action,
	ocu_ant,
	coeficientes,
	paquetes_entrantes
) {
	const descartados = calc_descartados(ocu_actual, paquetes_entrantes, action);
	return reward(descartados, ocu_actual, action, ocu_ant, coeficientes);
}
function calcular_ocu_actual(ocu_ant, paquetes_entrantes, action) {
	if (action == Action.PERMITIR) {
		ocu = Math.min(1.0, ocu_ant + paquetes_entrantes);
	}
	return ocu - (duration_step * vProcesamiento) / tamCola;
}
// Generar datos para el gráfico de superficie con una acción seleccionada
function generarDatosSuperficie(x, y, coeficientes, accion) {
	let recompensas = []; // Matriz de recompensas

	let minZ = Infinity;
	let maxZ = -Infinity;
	x.forEach((p) => {
		let filaZ = [];
		y.forEach((o) => {
			ocu_act = calcular_ocu_actual(o, p, accion);
			let recompensa = reward_funtion(ocu_act, accion, o, coeficientes, p); // Paquetes entrantes en %
			filaZ.push(recompensa);

			if (recompensa < minZ) minZ = recompensa;
			if (recompensa > maxZ) maxZ = recompensa;
		});

		recompensas.push(filaZ);
	});

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
		name: accion === Action.PERMITIR ? "Permitir" : "Denegar",
	};

	return trace;
}
function roundDecimal(numero, decimales) {
	const factor = Math.pow(10, decimales);
	return Math.round(numero * factor) / factor;
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
	const max_lim = document.getElementById("lim").value;

	for (let p = 0.0; p <= max_lim; p += max_lim / precision) {
		// Paquetes entrantes en %
		x.push(roundDecimal(p, 2));
	}
	for (let o = 0.0; o <= 1.0; o += 1 / precision) {
		//Ocupacion
		y.push(roundDecimal(o, 2));
	}
	// Si el checkbox de "Permitir" está marcado, creamos el gráfico correspondiente
	if (mostrarPermitir) {
		let trace = crearPlanoSuperficie(x, y, coeficientes, Action.PERMITIR);
		// Actualizamos el minZ y maxZ global
		if (trace.cmin < minZGlobal) minZGlobal = trace.cmin;
		if (trace.cmax > maxZGlobal) maxZGlobal = trace.cmax;
		traces.push(trace);
	}

	// Si el checkbox de "Denegar" está marcado, creamos el gráfico correspondiente
	if (mostrarDenegar) {
		let trace = crearPlanoSuperficie(x, y, coeficientes, Action.DENEGAR);
		if (trace.cmin < minZGlobal) minZGlobal = trace.cmin;
		if (trace.cmax > maxZGlobal) maxZGlobal = trace.cmax;
		traces.push(trace);
	}

	// Si no se selecciona ningún checkbox, mostramos un mensaje o mantenemos el gráfico vacío

	// Aplicamos el rango global de minZ y maxZ a ambos gráficos
	traces.forEach((trace) => {
		trace.cmin = minZGlobal;
		trace.cmax = maxZGlobal;
		trace.colorbar.tickvals = [minZGlobal, maxZGlobal];
		trace.colorbar.ticktext = [
			`${minZGlobal.toFixed(0)}`,
			`${maxZGlobal.toFixed(0)}`,
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
			text: "Reward en función de %  Paquetes entrantes y Ocupación",
			font: {},
		},
		scene: {
			xaxis: { title: { text: "% paquetes entrantes" } },
			yaxis: { title: { text: "% Ocupación" } },
			zaxis: { title: { text: "Recompensa" } },
		},
		margin: {
			b: 0,
			l: 0,
			r: 0,
			t: 40,
		},
		autosize: true,
	};
	Plotly.react("grafico3d", traces, layout, {
		responsive: true,
		scrollZoom: true,
		config: {
			scrollZoom: true,
			displayModeBar: true,
			staticPlot: false,
		},
	});
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
	const precision =
		parseInt(document.getElementById("precision").value, 10) - 1;
	crearGrafico3D(precision);
}
function actualizarSliders() {
	const ids = ["precision", "c", "c2", "c3", "c4", "lim"];

	for (let id of ids) {
		document.getElementById(`val-${id}`).textContent =
			document.getElementById(id).value;
	}
}

function actualizarGraficoDebounced(debounceTime = 100) {
	return debounce(() => {
		actualizarSliders();
		actualizarGrafico();
	}, debounceTime);
}


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

	crearGrafico3D();
});


// Verificar si estamos en un entorno Node.js
if (typeof module !== "undefined" && typeof module.exports !== "undefined") {
	module.exports = {
		reward,
		calc_descartados,
		Action,
		generarDatosSuperficie,
		crearPlanoSuperficie,
		roundDecimal,
		debounce,
		actualizarSliders,
		actualizarGrafico,
		actualizarGraficoDebounced,
		calcular_ocu_actual,
		tamCola,
		duration_step,
		vProcesamiento,
	};
}
