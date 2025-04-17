const fs = require("fs");
const path = require("path");

const {
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
	tamPaquete,
} = require("../../params/grafico3d.js");

if (
	!reward ||
	!calc_descartados ||
	!Action ||
	!generarDatosSuperficie ||
	!crearPlanoSuperficie ||
	!roundDecimal ||
	!debounce ||
	!actualizarSliders ||
	!actualizarGrafico ||
	!actualizarGraficoDebounced ||
	!calcular_ocu_actual ||
	!tamCola ||
	!duration_step ||
	!vProcesamiento ||
	!tamPaquete
) {
	console.error(
		"Error: Alguna de las importaciones desde 'grafico3d.js' no se encuentra correctamente exportada."
	);
	process.exit(1);
}

beforeEach(() => {
	const html = fs.readFileSync(
		path.resolve(__dirname, "../../params/Params.html"),
		"utf8"
	);
	document.body.innerHTML = html;

	// Simular la ejecución del script después de cargar el DOM
	require("../../params/grafico3d.js");
});

test("reward function must be a function", () => {
	expect(typeof reward).toBe("function"); // Asegurarse de que reward es una función
});
const testDescartadosCases = [
	{
		ocupacion: 0.5,
		paquetes_entrantes: 0.5,
		action: Action.PERMITIR,
		expected: 0.0,
	},
	{
		ocupacion: 0.0,
		paquetes_entrantes: 1.0,
		action: Action.PERMITIR,
		expected: 0.0,
	},
	{
		ocupacion: 1.0,
		paquetes_entrantes: 1.0,
		action: Action.PERMITIR,
		expected: 1.0 * tamCola,
	},
];
test.each(testDescartadosCases)(
	"calc_descartados(ocupacion: $ocupacion, paquetes_entrantes: $paquetes_entrantes, action: $action) debería retornar $expected",
	({ ocupacion, paquetes_entrantes, action, expected }) => {
		const result = calc_descartados(ocupacion, paquetes_entrantes, action);
		expect(result).toBe(expected);
	}
);

const testRewardCases = [
	{
		decartados: 0.5,
		ocu_actual: 0.5,
		action: Action.PERMITIR,
		ocu_ant: 0.0,
		coeficientes: { c: 0, c2: 0, c3: 0, c4: 0 ,c5: 0 },
		expected: 0.0,
	},
	{
		decartados: 1.0,
		ocu_actual: 0.5,
		action: Action.PERMITIR,
		ocu_ant: 0.0,
		coeficientes: { c: 0, c2: 0, c3: 0, c4: 0 ,c5: 0 },
		expected: 0.0,
	},
];
test.each(testRewardCases)(
	"reward(decartados: $decartados, ocu_actual: $ocu_actual, action: $action, ocu_ant: $ocu_ant, coeficientes: $coeficientes) debería retornar $expected",
	({ decartados, ocu_actual, action, ocu_ant, coeficientes, expected }) => {
		const result = reward(
			decartados,
			ocu_actual,
			action,
			ocu_ant,
			coeficientes
		);
		expect(result).toBe(expected);
	}
);

test("Action.DENEGAR constant should be defined and equal to 1", () => {
	expect(Action.DENEGAR).toBe(1);
});
test("Action.PERMITIR constant should be defined and equal to 0", () => {
	expect(Action.PERMITIR).toBe(0);
});

test("crearDatosSuperficie should return an array of arrays and x and y must be the same as parameters", () => {
	const x = [0.1, 0.2, 0.3];
	const y = [0.1, 0.2, 0.3];
	const coeficientes = { c: 1, c2: 1, c3: 1, c4: 1 , c5: 1 };

	let acciones = [Action.PERMITIR, Action.DENEGAR];

	acciones.forEach((action) => {
		const datos = generarDatosSuperficie(x, y, coeficientes, action);

		expect(Array.isArray(datos.recompensas)).toEqual(true);

		expect(typeof datos.minZ).toBe("number");
		expect(typeof datos.maxZ).toBe("number");

		expect(datos.recompensas.length).toBe(x.length);
		datos.recompensas.forEach((row) => {
			expect(Array.isArray(row)).toBe(true);
			expect(row.length).toBe(y.length);
		});
	});
});

test("crearPlanoSuperficie should return a trace", () => {
	const x = [0.1, 0.2, 0.3];
	const y = [0.1, 0.2, 0.3];
	const coeficientes = { c: 1, c2: 1, c3: 1, c4: 1 , c5: 1 };
	let acciones = [Action.PERMITIR, Action.DENEGAR];

	acciones.forEach((action) => {
		const trace = crearPlanoSuperficie(x, y, coeficientes, action);

		expect(Array.isArray(trace.x)).toEqual(true);
		expect(Array.isArray(trace.y)).toEqual(true);
		expect(Array.isArray(trace.z)).toEqual(true);

		expect(trace.x).toBe(x);
		expect(trace.y).toBe(y);

		expect(trace.z.length).toBe(x.length);
		trace.z.forEach((row) => {
			expect(Array.isArray(row)).toBe(true);
			expect(row.length).toBe(y.length);
		});
	});
});

const testRoundDecimalCases = [
	{ numero: 1.2345, decimales: 2, expected: 1.23 },
	{ numero: 1.2355, decimales: 2, expected: 1.24 },
	{ numero: 1.2, decimales: 3, expected: 1.2 },
	{ numero: 0.123456, decimales: 4, expected: 0.1235 },
	{ numero: -1.2345, decimales: 2, expected: -1.23 },
	{ numero: -1.2355, decimales: 2, expected: -1.24 },
	{ numero: 0, decimales: 2, expected: 0 },
	{ numero: 1.5, decimales: 0, expected: 2 },
	{ numero: 1.4, decimales: 0, expected: 1 },
];

test.each(testRoundDecimalCases)(
	"roundDecimal(numero: $numero, decimales: $decimales) debería retornar $expected",
	({ numero, decimales, expected }) => {
		const result = roundDecimal(numero, decimales);
		expect(result).toBe(expected);
	}
);

test("debounce should delay execution and use latest arguments", (done) => {
	const mockFunction = jest.fn();
	const context = { value: 42 };
	const debouncedFunction = debounce(
		function (...args) {
			expect(this).toBe(context);
			mockFunction(...args);
		}.bind(context),
		100
	);

	debouncedFunction(1);
	debouncedFunction(2);
	debouncedFunction(3);

	setTimeout(() => {
		expect(mockFunction).toHaveBeenCalledTimes(1);
		expect(mockFunction).toHaveBeenCalledWith(3);
		done();
	}, 150);
});

test("actualizarSliders should update the text content of elements with the corresponding slider values", () => {
	const ids = ["precision", "c", "c2", "c3", "c4", "c5", "lim"];
	let randomValues = [];

	ids.forEach((id) => {
		const slider = document.getElementById(id);
		let randomValue;
		if (slider.step >= 1.0) {
			randomValue = roundDecimal(
				Math.random() * (slider.max - slider.min) + parseInt(slider.min, 10),
				0
			);
		} else {
			randomValue = roundDecimal(
				Math.random() * (slider.max - slider.min) + parseFloat(slider.min, 10),
				2
			);
		}
		randomValues.push(randomValue);
	});

	ids.forEach((id, index) => {
		let slider = document.getElementById(id);
		slider.value = randomValues[index];
	});
	actualizarSliders();

	ids.forEach((id, index) => {
		const valueDisplay = document.getElementById(`val-${id}`);
		expect(Number(valueDisplay.textContent)).toBe(randomValues[index]);
	});
});

test("actualizarGrafico", () => {
	try {
		actualizarGrafico();
	} catch (error) {
		expect(error).toBeInstanceOf(ReferenceError);
	}
	try {
		document.getElementById("checkboxPermitir").checked = false;
		document.getElementById("checkboxDenegar").checked = false;
		actualizarGrafico();
	} catch (error) {
		expect(error).toBeInstanceOf(ReferenceError);
	}
});

test("DOM fully loaded and initialized", () => {
	document.addEventListener("DOMContentLoaded", () => {
		expect(document.body.innerHTML).not.toBe("");
	});
});

test("actualizarGraficoDebounced", () => {
	try {
		actualizarGraficoDebounced();
	} catch (error) {
		expect(error).toBeInstanceOf(ReferenceError);
	}
});

const testCalcularOcuActualCases = [
	{
		ocu_ant: 0.5,
		paquetes_entrantes: 0.3,
		action: Action.PERMITIR,
		expected: 0.8 - (duration_step * vProcesamiento) / (tamCola * tamPaquete)
	},
];
test.each(testCalcularOcuActualCases)(
	"calcular_ocu_actual(ocu_ant: $ocu_ant, paquetes_entrantes: $paquetes_entrantes, action: $action) debería retornar $expected",
	({ ocu_ant, paquetes_entrantes, action, expected }) => {
		const result = calcular_ocu_actual(ocu_ant, paquetes_entrantes, action);
		expect(result).toBe(expected);
	}
);

test("Ensure correct global variables are defined", () => {
	expect(typeof tamCola).toBe("number");
	expect(tamCola).toBeGreaterThan(0);

	expect(typeof duration_step).toBe("number");
	expect(duration_step).toBeGreaterThan(0);

	expect(typeof vProcesamiento).toBe("number");
	expect(vProcesamiento).toBeGreaterThan(0);

	expect(typeof tamPaquete).toBe("number");
	expect(tamPaquete).toBeGreaterThan(0);
});
