package molina.ia.tresEnRaya;

import molina.tresRaya.TresEnRaya;

public class JugadaTresEnRaya {
	private char[][] matriz;
	private int profundidad;
	private JugadaTresEnRaya[] hijos;
	private double puntuacion;
	private Integer movimiento;

	public JugadaTresEnRaya(char[][] matriz, int profundidad) {
		movimiento = null;
		this.matriz = matriz;
		this.profundidad = profundidad;
		generarHijos();
		puntuacion = calcularPuntuacion();
	}

	public JugadaTresEnRaya(char[][] matriz, int profundidad, int movimiento) {
		this.movimiento = movimiento;
		this.matriz = matriz;
		this.profundidad = profundidad;
		generarHijos();
		puntuacion = calcularPuntuacion();
	}

	public Integer getMovimiento() {
		return movimiento;
	}

	public double getPuntuacion() {
		return puntuacion;
	}

	public Integer getJugadaOptima() {
		return getMejorJugada().movimiento;
	}

	public JugadaTresEnRaya getMejorJugada() {
		JugadaTresEnRaya mejorJugada = null;
		if (hijos != null) {
			mejorJugada = hijos[0];
			double puntuacion = hijos[0].getPuntuacion();
			// si le toca jugar a la máquina tiene que coger el hijo con mayor
			// puntuación
			if (profundidad % 2 == 0) {
				for (int i = 1; i < hijos.length; i++)
					if (hijos[i].getPuntuacion() > puntuacion) {
						puntuacion = hijos[i].getPuntuacion();
						mejorJugada = hijos[i];
					}
			} else
				// pero si le toca al otro jugador tiene que coger al hijo con
				// menor puntuación
				for (int i = 1; i < hijos.length; i++)
					if (hijos[i].getPuntuacion() < puntuacion) {
						puntuacion = hijos[i].getPuntuacion();
						mejorJugada = hijos[i];
					}
		}
		return mejorJugada;
	}

	private int getCasillasLibres() {
		int contador = 0;
		for (int i = 0; i < TresEnRaya.TAM; i++)
			for (int j = 0; j < TresEnRaya.TAM; j++)
				if (matriz[i][j] == TresEnRaya.VACIO)
					contador++;
		return contador;
	}

	private void generarHijos() {
		if (profundidad < TresEnRaya.getDificultad() && !nodoTerminal()) {
			int tam = getCasillasLibres();
			if (tam > 0) {
				hijos = new JugadaTresEnRaya[tam];
				int pos = 0;
				for (int i = 0; i < TresEnRaya.TAM * TresEnRaya.TAM; i++) {
					int fila = i / TresEnRaya.TAM;
					int columna = i % TresEnRaya.TAM;
					if (matriz[fila][columna] == TresEnRaya.VACIO)
						hijos[pos++] = generarHijo(i);
				}
			}
		} else
			// es un nodo hoja
			hijos = null;
	}

	private JugadaTresEnRaya generarHijo(int pos) {
		char[][] newMatriz = new char[TresEnRaya.TAM][TresEnRaya.TAM];
		// copiamos la matriz
		for (int i = 0; i < newMatriz.length; i++)
			for (int j = 0; j < newMatriz[i].length; j++)
				newMatriz[i][j] = matriz[i][j];
		// y le modificamos un dato
		int fila = pos / TresEnRaya.TAM;
		int columna = pos % TresEnRaya.TAM;
		newMatriz[fila][columna] = fichaAColocar();
		return new JugadaTresEnRaya(newMatriz, profundidad + 1, pos);
	}

	private char fichaAColocar() {
		if (profundidad % 2 == 0)
			return TresEnRaya.JUGADOR2;
		return TresEnRaya.JUGADOR1;
	}

	private double calcularPuntuacion() {
		// si la jugada tiene hijos, la puntuación será la mejor o peor de las
		// puntuaciones de sus hijos, dependiendo de si se analiza a una
		// profundidad par o impar por el criterio de MINIMAX
		// SI NO TIENE HIJOS SE APLICA EL ALGORITMO GENÉRICO PARA UN NODO HOJA
		if (hijos == null) {
			return evaluarPosicion();
		} else {
			if (profundidad % 2 == 0)
				return maximoValor();
			else
				return minimoValor();
		}
	}

	private double minimoValor() {
		double minimaPuntuacion = hijos[0].getPuntuacion();
		for (int i = 1; i < hijos.length; i++)
			if (hijos[i].getPuntuacion() < minimaPuntuacion)
				minimaPuntuacion = hijos[i].getPuntuacion();
		return minimaPuntuacion;
	}

	private double maximoValor() {
		double maximaPuntuacion = hijos[0].getPuntuacion();
		for (int i = 1; i < hijos.length; i++)
			if (hijos[i].getPuntuacion() > maximaPuntuacion)
				maximaPuntuacion = hijos[i].getPuntuacion();
		return maximaPuntuacion;
	}

	private boolean nodoTerminal() {
		int contadorJugador1 = 0;
		int contadorJugador2 = 0;
		// FILAS
		for (int i = 0; i < TresEnRaya.TAM; i++) {
			contadorJugador1 = 0;
			contadorJugador2 = 0;
			for (int j = 0; j < TresEnRaya.TAM; j++)
				switch (matriz[i][j]) {
				case TresEnRaya.JUGADOR1:
					contadorJugador1++;
					break;
				case TresEnRaya.JUGADOR2:
					contadorJugador2++;
					break;
				}
			if (contadorJugador2 == TresEnRaya.TAM
					|| contadorJugador1 == TresEnRaya.TAM)
				return true;
		}

		// COLUMNAS
		for (int i = 0; i < TresEnRaya.TAM; i++) {
			contadorJugador1 = 0;
			contadorJugador2 = 0;
			for (int j = 0; j < TresEnRaya.TAM; j++)
				switch (matriz[j][i]) {
				case TresEnRaya.JUGADOR1:
					contadorJugador1++;
					break;
				case TresEnRaya.JUGADOR2:
					contadorJugador2++;
					break;
				}
			if (contadorJugador2 == TresEnRaya.TAM
					|| contadorJugador1 == TresEnRaya.TAM)
				return true;
		}

		// DIAGONAL PRINCIPAL
		contadorJugador1 = 0;
		contadorJugador2 = 0;
		for (int i = 0; i < TresEnRaya.TAM; i++) {
			switch (matriz[i][i]) {
			case TresEnRaya.JUGADOR1:
				contadorJugador1++;
				break;
			case TresEnRaya.JUGADOR2:
				contadorJugador2++;
				break;
			}
			if (contadorJugador2 == TresEnRaya.TAM
					|| contadorJugador1 == TresEnRaya.TAM)
				return true;
		}

		// DIAGONAL SECUNDARIA
		contadorJugador1 = 0;
		contadorJugador2 = 0;
		for (int i = 0, j = TresEnRaya.TAM - 1; i < TresEnRaya.TAM && j >= 0; i++, j--) {
			switch (matriz[i][j]) {
			case TresEnRaya.JUGADOR1:
				contadorJugador1++;
				break;
			case TresEnRaya.JUGADOR2:
				contadorJugador2++;
				break;
			}
			if (contadorJugador2 == TresEnRaya.TAM
					|| contadorJugador1 == TresEnRaya.TAM)
				return true;
		}
		return false;
	}

	private double evaluarPosicion() {
		double puntuacion = 0d;
		int contadorJugador1 = 0;
		int contadorJugador2 = 0;
		// FILAS
		for (int i = 0; i < TresEnRaya.TAM; i++) {
			contadorJugador1 = 0;
			contadorJugador2 = 0;
			for (int j = 0; j < TresEnRaya.TAM; j++)
				switch (matriz[i][j]) {
				case TresEnRaya.JUGADOR1:
					contadorJugador1++;
					break;
				case TresEnRaya.JUGADOR2:
					contadorJugador2++;
					break;
				}
			// primero vemos la máquina puede ganar
			if (contadorJugador2 == TresEnRaya.TAM)
				return Double.POSITIVE_INFINITY;
			else if (contadorJugador1 == TresEnRaya.TAM)
				return Double.NEGATIVE_INFINITY;
			else if (contadorJugador1 == 0)
				puntuacion++;
		}

		// COLUMNAS
		for (int i = 0; i < TresEnRaya.TAM; i++) {
			contadorJugador1 = 0;
			contadorJugador2 = 0;
			for (int j = 0; j < TresEnRaya.TAM; j++)
				switch (matriz[j][i]) {
				case TresEnRaya.JUGADOR1:
					contadorJugador1++;
					break;
				case TresEnRaya.JUGADOR2:
					contadorJugador2++;
					break;
				}
			if (contadorJugador2 == TresEnRaya.TAM)
				return Double.POSITIVE_INFINITY;
			else if (contadorJugador1 == TresEnRaya.TAM)
				return Double.NEGATIVE_INFINITY;
			else if (contadorJugador1 == 0)
				puntuacion++;
		}

		// DIAGONAL PRINCIPAL
		contadorJugador1 = 0;
		contadorJugador2 = 0;
		for (int i = 0; i < TresEnRaya.TAM; i++) {
			switch (matriz[i][i]) {
			case TresEnRaya.JUGADOR1:
				contadorJugador1++;
				break;
			case TresEnRaya.JUGADOR2:
				contadorJugador2++;
				break;
			}
			if (contadorJugador2 == TresEnRaya.TAM)
				return Double.POSITIVE_INFINITY;
			else if (contadorJugador1 == TresEnRaya.TAM)
				return Double.NEGATIVE_INFINITY;
			else if (contadorJugador1 == 0)
				puntuacion++;
		}

		// DIAGONAL SECUNDARIA
		contadorJugador1 = 0;
		contadorJugador2 = 0;
		for (int i = 0, j = TresEnRaya.TAM - 1; i < TresEnRaya.TAM && j >= 0; i++, j--) {
			switch (matriz[i][j]) {
			case TresEnRaya.JUGADOR1:
				contadorJugador1++;
				break;
			case TresEnRaya.JUGADOR2:
				contadorJugador2++;
				break;
			}
			if (contadorJugador2 == TresEnRaya.TAM)
				return Double.POSITIVE_INFINITY;
			else if (contadorJugador1 == TresEnRaya.TAM)
				return Double.NEGATIVE_INFINITY;
			else if (contadorJugador1 == 0)
				puntuacion++;
		}
		return puntuacion;
	}
}
