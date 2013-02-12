package molina.ia.generadorSudokus;

import java.util.ArrayList;
import java.util.Random;
import molina.sudoku.*;

public class GeneradorSudoku {
	private int[][] matriz;

	public GeneradorSudoku() {
		matriz = new int[Sudoku.TAM][Sudoku.TAM];
		generarSudoku();
	}

	public int[][] generarSolucionParcial(Dificultad dif) {
		int dificultad = 0;
		switch (dif) {
		case DIFICIL:
			dificultad = 0;
			break;
		case FACIL:
			dificultad = 2;
			break;
		case NORMAL:
			dificultad = 1;
			break;
		default:
			break;
		}
		Random random = new Random();
		int panel[][] = new int[Sudoku.TAM][Sudoku.TAM];
		for (int i = 0; i < Sudoku.TAM; ++i)
			for (int j = 0; j < Sudoku.TAM; ++j)
				panel[i][j] = matriz[i][j];
		// y luego ponemos los ceros en determinados sitios
		for (int i = 0; i < 51 - 3 * dificultad; ++i) {
			int j, k;
			do {
				j = random.nextInt(Sudoku.TAM);
				k = random.nextInt(Sudoku.TAM);
			} while (panel[j][k] == 0);
			panel[j][k] = 0;
		}
		return panel;
	}

	public int[][] getMatriz() {
		return matriz;
	}

	private void generarSudoku() {
		Random random = new Random();
		int valor = 0;
		// inicializamos aleatoriamente la primera fila
		for (int i = 0; i < Sudoku.TAM; i++) {
			do {
				valor = 1 + random.nextInt(Sudoku.TAM);
			} while (!valorValido(0, i, valor));
			matriz[0][i] = valor;
		}
		backtracking(Sudoku.TAM);
	}

	private boolean backtracking(int codigo) {
		// si hemos rellenado el sudoku devolvemos true
		if (sudokuCompleto())
			return true;
		// desglosamos el codigo de la casilla en fila y columna
		int fila = codigo / Sudoku.TAM;
		int columna = codigo % Sudoku.TAM;
		boolean valido = false;
		Random random = new Random();
		ArrayList<Integer> valores = new ArrayList<Integer>(Sudoku.TAM);
		for (int i = 1; i < Sudoku.TAM + 1; i++)
			valores.add(i);
		// le damos los posibles valores a esa casilla
		for (int i = 0; i < Sudoku.TAM; i++) {
			int valor = valores.remove(random.nextInt(valores.size()));
			if (valorValido(fila, columna, valor)) {
				matriz[fila][columna] = valor;
				valido = backtracking(codigo + 1);
				if (valido)
					return true;
				else
					matriz[fila][columna] = 0;
			}
		}
		return false;
	}

	private boolean sudokuCompleto() {
		for (int i = 0; i < Sudoku.TAM; i++)
			for (int j = 0; j < Sudoku.TAM; j++)
				if (matriz[i][j] == 0)
					return false;
		return true;
	}

	private boolean valorValido(int fila, int columna, int valor) {
		return valorValidoFila(fila, valor)
				&& valorValidoColumna(columna, valor)
				&& valorValidoSector(fila, columna, valor);
	}

	private boolean valorValidoSector(int fila, int columna, int valor) {
		int I = (fila / 3) * 3;
		int J = (columna / 3) * 3;
		for (int i = I; i < I + 3; i++)
			for (int j = J; j < J + 3; j++)
				if (matriz[i][j] == valor)
					return false;
		return true;
	}

	private boolean valorValidoColumna(int columna, int valor) {
		for (int i = 0; i < Sudoku.TAM; i++)
			if (matriz[i][columna] == valor)
				return false;
		return true;
	}

	private boolean valorValidoFila(int fila, int valor) {
		for (int i = 0; i < Sudoku.TAM; i++)
			if (matriz[fila][i] == valor)
				return false;
		return true;
	}
}
