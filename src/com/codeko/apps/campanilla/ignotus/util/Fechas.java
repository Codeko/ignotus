package com.codeko.apps.campanilla.ignotus.util;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.Vector;

//TODO Limpiar de funciones inutiles y psar a Util.java
//TODO I18N
public class Fechas {
	public static final int MILLISECONDS_PER_SECOND = 1000;
	public static final int MILLISECONDS_PER_DAY = 86400000;
	public static final int MILLISECONDS_PER_HOUR = 3600000;
	public static final int MILLISECONDS_PER_MINUTE = 60000;

	static Integer diasEnPrimeraSemana = null;
	public static final String[] MESES = { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" };
	public static final String[] MESES_CON_TODOS = { "Todos", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" };
	public static final String[] DIAS_SEMANA = { "Domingo", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sábado" };

	public static String getNombreMesActual() {
		GregorianCalendar cal = new GregorianCalendar();
		return getNombreMes(cal.get(GregorianCalendar.MONTH));
	}

	public static String getNombreMes(int mes) {
		if (mes > -1 && mes < MESES.length) {
			return MESES[mes];
		}
		return "";
	}

	public static String getNombreDiaSemana(int dia) {
		dia--;
		if (dia > -1 && dia < DIAS_SEMANA.length) {
			return DIAS_SEMANA[dia];
		}
		return "";
	}

	public static long getDiferenciaEn(GregorianCalendar start, GregorianCalendar end, int tipo) {
		long difference = end.getTimeInMillis() - start.getTimeInMillis();
		long tiempo = 0;
		switch (tipo) {
			case GregorianCalendar.MILLISECOND:
				return difference;
			case GregorianCalendar.DATE:
				tiempo = difference / MILLISECONDS_PER_DAY;
				break;
			case GregorianCalendar.HOUR:
				tiempo = difference / MILLISECONDS_PER_HOUR;
				break;
			case GregorianCalendar.MINUTE:
				tiempo = difference / MILLISECONDS_PER_MINUTE;
				break;
			case GregorianCalendar.SECOND:
				tiempo = difference / MILLISECONDS_PER_SECOND;
				break;
		}
		return tiempo;
	}

	public static String getMilisegundosATexto(long milisegundos) {
		return getMilisegundosATexto(milisegundos, true, true, true);
	}

	public static String getMilisegundosATexto(long milisegundos, boolean dias, boolean horas, boolean minutos) {
		int days = (int) Math.floor(milisegundos / MILLISECONDS_PER_DAY);
		if (dias) {
			milisegundos = milisegundos - (days * MILLISECONDS_PER_DAY);
		}
		int hours = (int) Math.floor(milisegundos / MILLISECONDS_PER_HOUR);
		if (horas) {
			milisegundos = milisegundos - (hours * MILLISECONDS_PER_HOUR);
		}
		int minutes = (int) Math.floor(milisegundos / MILLISECONDS_PER_MINUTE);
		if (minutos) {
			milisegundos = milisegundos - (minutes * MILLISECONDS_PER_MINUTE);
		}
		// int seconds = (int) Math.floor(milisegundos / MILLISECONDS_PER_SECOND);
		StringBuffer buf = new StringBuffer();
		if (dias) {
			if (days == 1) {
				buf.append("0").append(days).append(" día ");
			} else if (days != 0 && (days + "").length() == 1) {
				buf.append("0").append(days).append(" días ");
			} else if (days != 0) {
				buf.append(days).append(" días ");
			}
		}
		if (horas) {
                    
			buf.append(Util.lPad(hours + "", '0', 2)).append("h ");
		}
		if (minutos) {
			buf.append(Util.lPad(minutes + "", '0', 2)).append("m");
		}
		return buf.toString();
	}

	public static String calcularDiferencia(GregorianCalendar start, GregorianCalendar end) {
		return calcularDiferencia(start, end, true, true, true);
	}

	public static String calcularDiferencia(GregorianCalendar start, GregorianCalendar end, boolean dias, boolean horas, boolean minutos) {
		long difference = 0;
		if (start != null && end != null) {
			long fin = end.getTimeInMillis();
			long inicio = start.getTimeInMillis();
			difference = fin - inicio;
		}
		return getMilisegundosATexto(difference, dias, horas, minutos);
	}

	public static long getDiferenciaEnMilis(GregorianCalendar start, GregorianCalendar end) {
		long difference = 0;
		if (start != null && end != null) {
			long fin = end.getTimeInMillis();
			long inicio = start.getTimeInMillis();
			difference = fin - inicio;
		}
		return difference;
	}

	public static int getAnoActual() {
		GregorianCalendar cal = new GregorianCalendar();
		return cal.get(GregorianCalendar.YEAR);
	}

	public static int getHoraActual() {
		GregorianCalendar cal = new GregorianCalendar();
		return cal.get(GregorianCalendar.HOUR_OF_DAY);
	}

	public static int getMinutosActual() {
		GregorianCalendar cal = new GregorianCalendar();
		return cal.get(GregorianCalendar.MINUTE);
	}

	public static int getSegundosActual() {
		GregorianCalendar cal = new GregorianCalendar();
		return cal.get(GregorianCalendar.SECOND);
	}

	public static String getAnoSimpleActual() {
		GregorianCalendar cal = new GregorianCalendar();
		String ano = (cal.get(GregorianCalendar.YEAR) + "").substring(2);
		return ano;
	}

	public static int getMesActual() {
		GregorianCalendar cal = new GregorianCalendar();
		return cal.get(GregorianCalendar.MONTH);
	}

	public static int getDiaActual() {
		GregorianCalendar cal = new GregorianCalendar();
		return cal.get(GregorianCalendar.DAY_OF_MONTH);
	}

	public static Vector getVectorDiasMesActual() {
		return getVectorDiasMes(new GregorianCalendar());
	}

	public static Vector<Integer> getVectorDiasMes(GregorianCalendar cal) {
		Vector<Integer> dias = new Vector<Integer>(31);
		int numDias = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		for (int i = 1; i < numDias + 1; i++) {
			dias.add(i);
		}
		return dias;
	}

	public static Vector getVectorDiasMesActualFormateado() {
		Vector<String> dias = new Vector<String>(31);
		GregorianCalendar cal = new GregorianCalendar();
		int numDias = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		for (int i = 1; i < numDias + 1; i++) {
			dias.add(Util.rPad(i + "", ' ', 3));
		}
		return dias;
	}

	public static String getFecha() {
		GregorianCalendar cal = new GregorianCalendar();
		int dia = cal.get(Calendar.DAY_OF_MONTH);
		int mes = cal.get(Calendar.MONTH) + 1;
		int ano = cal.get(Calendar.YEAR);
		String fecha = "" + dia;
		if (dia < 10) {
			fecha = "0" + dia;
		}
		if (mes < 10) {
			fecha += "-0" + mes;
		} else {
			fecha += "-" + mes;
		}
		fecha += "-" + ano;
		return fecha;
	}

	public static GregorianCalendar getFecha(java.util.Date fecha) {
		if (fecha == null) {
			return null;
		}
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(fecha);
		return cal;
	}

	public static String getHora() {
		GregorianCalendar cal = new GregorianCalendar();
		int h = cal.get(Calendar.HOUR_OF_DAY);
		int m = cal.get(Calendar.MINUTE);
		String hora = "" + h;
		if (h < 10) {
			hora = "0" + h;
		}
		if (m < 10) {
			hora += ":0" + m;
		} else {
			hora += ":" + m;
		}
		return hora;

	}

	public static boolean esFechaValida(String fecha) {
		// String fecha = null;
		if (fecha == null || fecha.replaceAll("-", "").trim().length() < 1) {
			return false;
		}
		StringTokenizer tk = new StringTokenizer(fecha, "-");
		String dia = tk.nextToken().trim();
		if (dia.equals("")) {
			dia = "0";
		}
		String mes = tk.nextToken().trim();
		if (mes.equals("")) {
			mes = "0";
		}
		String ano = tk.nextToken().trim();
		if (ano.equals("")) {
			ano = "0";
		}
		// GregorianCalendar cal=new GregorianCalendar(,,);
		// System.out.println(dia+"."+mes+"."+ano);
		boolean ret = esFechaValida(new Integer(dia).intValue(), new Integer(mes).intValue(), new Integer(ano).intValue());

		return ret;
	}

	public static boolean esFechaMysqlValida(String fechaMysql) {
		// String fecha = null;
		if (fechaMysql == null || fechaMysql.replaceAll("-", "").trim().length() < 1) {
			return false;
		}
		String[] tk =fechaMysql.split("-");
		if(tk.length<3) {
			return false;
		}
		String ano = tk[0];
		String mes = tk[1];
		String dia = tk[2];

		if (ano.equals("")) {
			ano = "0";
		}

		if (mes.equals("")) {
			mes = "0";
		}

		if (dia.equals("")) {
			dia = "0";
		}
		// GregorianCalendar cal=new GregorianCalendar(,,);
		// System.out.println(dia+"."+mes+"."+ano);
		boolean ret = esFechaValida(Util.getInt(dia), Util.getInt(mes),Util.getInt(ano));

		return ret;
	}

	public static boolean esFechaValida(int d, int m, int a) {

		int dia = d;
		int mes = m;
		int ano = a;
		boolean valida;
		if (dia < 1 || dia > 31 || mes < 1 || mes > 12) {
			valida = false;
		} else if ((mes == 2 || mes == 4 || mes == 6 || mes == 9 || mes == 11) && dia > 30) {
			valida = false;
		} else if (mes == 2 && dia > 29) {
			valida = false;
		} else if (mes == 2 && dia > 28 && !esBisiesto(ano)) {
			valida = false;
		} else {
			valida = true;
		}
		return valida;
	}

	/**
	 * Funcion que nos valida la hora y los minutos
	 */

	public static boolean esHoraValida(int hora, int minutos) {
		boolean valida = true;
		if (hora > 23 || hora < 0) {
			valida = false;
		} else if (minutos > 59 || minutos < 0) {
			valida = false;
		}
		return valida;
	}

	public static boolean esBisiesto(int ano) {
		boolean bisiesto;
		if (ano % 4 != 0) {
			bisiesto = false;
		} else if (ano % 400 == 0) {
			bisiesto = true;
		} else if (ano % 100 == 0) {
			bisiesto = false;
		} else {
			bisiesto = true;
		}
		return bisiesto;
	}

	public static String getFechaMysql(Date fecha) {
		if (fecha == null) {
			return "";
		}
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(fecha);
		return getFechaMysql(cal);
	}

	public static String getFechaMysql(GregorianCalendar cal) {
		if (cal == null) {
			return "";
		}
		int dia = cal.get(Calendar.DAY_OF_MONTH);
		int mes = cal.get(Calendar.MONTH) + 1;
		int ano = cal.get(Calendar.YEAR);
		// System.out.println("FECHA:"+dia+"."+mes+"."+ano);
		String fecha = "" + ano;
		if (mes < 10) {
			fecha += "-0" + mes;
		} else {
			fecha += "-" + mes;
		}
		if (dia < 10) {
			fecha += "-0" + dia;
		} else {
			fecha += "-" + dia;
		}
		return fecha;
	}

	public static String getFechaHoraMysql(GregorianCalendar cal) {
		int dia = cal.get(Calendar.DAY_OF_MONTH);
		int mes = cal.get(Calendar.MONTH) + 1;
		int ano = cal.get(Calendar.YEAR);
		int hora = cal.get(Calendar.HOUR_OF_DAY);
		int min = cal.get(Calendar.MINUTE);
		// System.out.println("FECHA:"+dia+"."+mes+"."+ano);
		String fecha = "" + ano;
		if (mes < 10) {
			fecha += "-0" + mes;
		} else {
			fecha += "-" + mes;
		}
		if (dia < 10) {
			fecha += "-0" + dia;
		} else {
			fecha += "-" + dia;
		}
		fecha += " " + Util.lPad(hora + "", '0', 2) + ":" + Util.lPad(min + "", '0', 2) + ":00";
		return fecha;
	}

	public static String getHoraMysql(Date fecha) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(fecha);
		return getHoraMysql(cal);
	}

	public static String getHoraMysql() {
		return getHoraMysql(new GregorianCalendar());
	}

	public static String getHoraMysql(GregorianCalendar cal) {
		if(cal==null) {
			return "";
		}
		int h = cal.get(Calendar.HOUR_OF_DAY);
		int m = cal.get(Calendar.MINUTE);
		int s = cal.get(Calendar.SECOND);
		String hora = Util.lPad(h + "", '0', 2) + ":" + Util.lPad(m + "", '0', 2) + ":" + Util.lPad(s + "", '0', 2);
		return hora;
	}

	public static String getFechaMysql() {
		return getFechaMysql(new GregorianCalendar());
	}

	public static GregorianCalendar asignarHora(GregorianCalendar fecha, GregorianCalendar hora) {
		if (fecha == null) {
			return fecha;
		}
		if (hora == null) {
			fecha.set(GregorianCalendar.HOUR_OF_DAY, 0);
			fecha.set(GregorianCalendar.MINUTE, 0);
			fecha.set(GregorianCalendar.SECOND, 0);
			return fecha;
		}
		int h = hora.get(GregorianCalendar.HOUR_OF_DAY);
		int m = hora.get(GregorianCalendar.MINUTE);
		fecha.set(GregorianCalendar.HOUR_OF_DAY, h);
		fecha.set(GregorianCalendar.MINUTE, m);
		fecha.set(GregorianCalendar.SECOND, 0);
		return fecha;
	}

	/**
	 * Compara dos fechas por a�o mes y dia
	 *
	 * @param cal1
	 *          GregorianCalendar fecha 1
	 * @param cal2
	 *          GregorianCalendar fecha 2
	 * @return int Devuelve 0 si son iguales 1 si la fecha 1 es mayor, -1 si la
	 *         fecha 2 es mayor
	 */
	public static int comparacionSimple(GregorianCalendar cal1, GregorianCalendar cal2) {
		int mes1 = cal1.get(GregorianCalendar.MONTH);
		int ano1 = cal1.get(GregorianCalendar.YEAR);
		int dia1 = cal1.get(GregorianCalendar.DAY_OF_MONTH);
		int mes2 = cal2.get(GregorianCalendar.MONTH);
		int ano2 = cal2.get(GregorianCalendar.YEAR);
		int dia2 = cal2.get(GregorianCalendar.DAY_OF_MONTH);

		if (ano1 > ano2) {
			return 1;
		} else if (ano2 > ano1) {
			return -1;
		} else if (mes1 > mes2) {
			return 1;
		} else if (mes2 > mes1) {
			return -1;
		} else if (dia1 > dia2) {
			return 1;
		} else if (dia2 > dia1) {
			return -1;
		} else {
			return 0;
		}
	}

	public static boolean esPasado(GregorianCalendar cal) {
		GregorianCalendar cal2 = new GregorianCalendar();
		return comparacionSimple(cal, cal2) == -1;
	}

	public static String getFechaFormateadaPresentacion(GregorianCalendar cal) {
		return getFechaFormateadaPresentacion(cal, "-");
	}

	public static String getFechaHoraFormateadaPresentacion(GregorianCalendar cal) {
		if (cal == null) {
			return "";
		}
		return Util.lPad(cal.get(Calendar.DAY_OF_MONTH) + "", '0', 2) + "-" + Util.lPad((cal.get(Calendar.MONTH) + 1) + "", '0', 2) + "-" + (Util.lPad(cal.get(Calendar.YEAR) + "", '0', 4)).substring(2) + " " + Util.lPad(cal.get(Calendar.HOUR_OF_DAY) + "", '0', 2) + ":" + Util.lPad(cal.get(Calendar.MINUTE) + "", '0', 2);
	}

	public static String getFechaHoraTextoNatural(GregorianCalendar cal) {
		if (cal == null) {
			return "";
		}
		StringBuilder sb=new StringBuilder();
		sb.append(cal.get(Calendar.DAY_OF_MONTH));
		sb.append(" de ");
		sb.append(getNombreMes(cal.get(Calendar.MONTH)));
		if(cal.get(Calendar.YEAR)!=getAnoActual()) {
			sb.append(" de ");
			sb.append(getNombreMes(cal.get(Calendar.YEAR)));
		}
		sb.append(" a las ");
		sb.append(cal.get(Calendar.HOUR_OF_DAY));
		sb.append(":");
		sb.append(cal.get(Calendar.MINUTE));
		return sb.toString();
	}

	public static String getFechaFormateadaPresentacion(GregorianCalendar cal, String sep) {
		if (cal == null) {
			return "";
		}
		return Util.lPad(cal.get(Calendar.DAY_OF_MONTH) + "", '0', 2) + sep + Util.lPad((cal.get(Calendar.MONTH) + 1) + "", '0', 2) + sep + (Util.lPad(cal.get(Calendar.YEAR) + "", '0', 4)).substring(2);
	}

	public static String getFechaFormateadaPresentacion4DigitosAno(Date date) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		return getFechaFormateadaPresentacion4DigitosAno(cal);
	}

	public static String getFechaFormateadaPresentacion4DigitosAno(GregorianCalendar cal) {
		if (cal == null) {
			return "";
		}
		return Util.lPad(cal.get(Calendar.DAY_OF_MONTH) + "", '0', 2) + "/" + Util.lPad((cal.get(Calendar.MONTH) + 1) + "", '0', 2) + "/" + (Util.lPad(cal.get(Calendar.YEAR) + "", '0', 4));
	}

	public static String getFechaFormateadaPresentacion(java.util.Date cal) {
		if (cal == null) {
			return "";
		}
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(cal);
		return getFechaFormateadaPresentacion(c);
	}

	public static String getFechaHoraFormateadaPresentacion(java.util.Date cal) {
		if (cal == null) {
			return "";
		}
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(cal);
		return getFechaHoraFormateadaPresentacion(c);
	}

	public static int getSemana() {
		return getSemana(new GregorianCalendar());
	}

	public static int getSemana(GregorianCalendar cal) {
		if (cal == null) {
			return 0;
		}
		GregorianCalendar cal2 = (GregorianCalendar) cal.clone();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		// cal2.setMinimalDaysInFirstWeek(getDiasEnPrimeraSemana());
		cal2.setFirstDayOfWeek(Calendar.MONDAY);
		int semana = cal2.get(Calendar.WEEK_OF_YEAR);
		/*
		 * if(getDiasEnPrimeraSemana()!=1){ int mes=cal2.get(cal2.MONTH); if(mes==0 &&
		 * semana>50){ semana=0; } }
		 */
		return semana;
	}

	public static GregorianCalendar getFechaParaSemana(int semana) {
		return getFechaParaSemana(semana, 0);
	}

	public static GregorianCalendar getFechaParaSemana(int semana, int ano) {
		GregorianCalendar cal = new GregorianCalendar();
		if (ano == 0) {
			ano = cal.get(Calendar.YEAR);
		}
		// cal.setMinimalDaysInFirstWeek(getDiasEnPrimeraSemana());
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.set(Calendar.YEAR, ano);
		cal.set(Calendar.WEEK_OF_YEAR, semana);
		/*
		 * if((semana==1 && getDiasEnPrimeraSemana()==1) || (semana==0 &&
		 * getDiasEnPrimeraSemana()!=1)){ cal.set(cal.DAY_OF_YEAR,1);
		 * cal.set(cal.MONTH,0); }else if(semana==1 && getDiasEnPrimeraSemana()!=1){
		 * semana++; cal.add(cal.DAY_OF_YEAR, ( (semana - 1) * 7) + 1);
		 * cal.set(cal.DAY_OF_WEEK, cal.MONDAY); }else{ cal.add(cal.DAY_OF_YEAR, (
		 * (semana - 1) * 7) + 1); cal.set(cal.DAY_OF_WEEK, cal.MONDAY); }
		 */
		return cal;
	}

	/**
	 * Funcion que nos da la comparacion de las dos fechas retorna -1 si comparada
	 * es menor que fecha retorna 0 si son iguales retorna 1 si comparda es mayor
	 * que fecha
	 *
	 * @param gc1
	 *          GregorianCalendar
	 * @param gc2
	 *          GregorianCalendar
	 * @return int
	 */
	public static int comparaFechaBasica(GregorianCalendar comparada, GregorianCalendar fecha) {
		int comparacion = 0;
		// Variables para gc1
		int ano1 = comparada.get(Calendar.YEAR);
		int mes1 = comparada.get(Calendar.MONTH);
		int dia1 = comparada.get(Calendar.DAY_OF_MONTH);

		// Variables para el gc2
		int ano2 = fecha.get(Calendar.YEAR);
		int mes2 = fecha.get(Calendar.MONTH);
		int dia2 = fecha.get(Calendar.DAY_OF_MONTH);

		if (ano1 == ano2) {
			if (mes1 == mes2) {
				if (dia1 == dia2) {
					comparacion = 0;
				} else if (dia1 < dia2) {
					comparacion = -1;
				} else if (dia1 > dia2) {
					comparacion = 1;
				}
			} else if (mes1 < mes2) {
				comparacion = -1;
			} else if (mes1 > mes2) {
				comparacion = 1;
			}
		} else if (ano1 < ano2) {
			comparacion = -1;
		} else if (ano1 > ano2) {
			comparacion = 1;
		}
		return comparacion;
	}

	/**
	 * Funcion que nos va a pasar la fecha de String a gregorian
	 *
	 * @param fecha
	 *          String
	 * @param separador
	 *          String
	 * @return GregorianCalendar
	 */
	public static GregorianCalendar parseFechaMysql(String fecha) {
		if (fecha == null || fecha.trim().equals("")) {
			return null;
		}
		StringTokenizer tk = new StringTokenizer(fecha, "-");
		String ano = tk.nextToken().trim();
		if (ano.equals("")) {
			ano = "0000";
		}
		String mes = tk.nextToken().trim();
		if (mes.equals("")) {
			mes = "00";
		}
		String dia = tk.nextToken().trim();
		if (dia.equals("")) {
			dia = "00";
		}
		GregorianCalendar cal = new GregorianCalendar(new Integer(ano).intValue(), new Integer(mes).intValue() - 1, new Integer(dia).intValue());
		return cal;
	}

	/**
	 * Funci�n para devolver el a�o en formato de 4 d�gitos, en caso de que sean 2
	 * en el formato original Si los dos d�gitos del a�o es superior a 50,
	 * devuelve el a�o a partir de 1900; En caso cntrario en formato de a�o 2000
	 * en adelante
	 */

	public static int getAnoCompleto(int ano) {
		if (Integer.toString(ano).length() == 2) {
			if (ano > 50) {
				// System.out.println("fechas:"+Integer.parseInt("19"+Integer.toString(ano)));
				return Util.getInt("19" + ano);
			}
			return Util.getInt("20" + ano);
		} else if (Integer.toString(ano).length() == 1) {
			return Integer.parseInt("200" + Integer.toString(ano));
		} else {
			return ano;
		}
	}

	public static GregorianCalendar parseDateYYMMDD(String fecha) {
		if (fecha == null || fecha.trim().length() < 1) {
			return null;
		}
		int ano = Util.getInt(fecha.substring(0, 2));
		ano = getAnoCompleto(ano);
		int mes = Util.getInt(fecha.substring(2, 4));
		// El mes lo ponemos en forma 0-11
		mes--;
		int dia = Util.getInt(fecha.substring(4));
		return new GregorianCalendar(ano, mes, dia);
	}

	public static GregorianCalendar parseDateEspanol(String fecha) {
		return parseDateEspanol(fecha, "-");
	}

	public static GregorianCalendar parseDateEspanol(String fecha, String separador) {
		if (fecha == null || fecha.replaceAll(separador, "").trim().length() < 1) {
			return null;
		}
		StringTokenizer tk = new StringTokenizer(fecha, separador);
		String dia = tk.nextToken().trim();
		if (dia.equals("")) {
			dia = "0";
		}
		String mes = tk.nextToken().trim();
		if (mes.equals("")) {
			mes = "0";
		}
		String ano = tk.nextToken().trim();
		if (ano.equals("")) {
			ano = "0";
		}
		GregorianCalendar cal = new GregorianCalendar(new Integer(ano).intValue(), new Integer(mes).intValue() - 1, new Integer(dia).intValue());
		return cal;
	}

	public static String getTextoFechaFormal(String poblacion) {
		return getTextoFechaFormal(new GregorianCalendar(), poblacion);
	}

	public static String getTextoFechaFormal(Date fecha, String poblacion) {
		GregorianCalendar cal = null;
		if (fecha != null) {
			cal = new GregorianCalendar();
			cal.setTime(fecha);
		}
		return getTextoFechaFormal(cal, poblacion);
	}

	public static String getTextoFechaFormal(GregorianCalendar fecha, String poblacion) {
		String texto = poblacion;
		if (fecha != null) {
			texto += " a " + fecha.get(GregorianCalendar.DAY_OF_MONTH) + " de " + getNombreMes(fecha.get(GregorianCalendar.MONTH)) + " de " + fecha.get(GregorianCalendar.YEAR);
		}
		return texto;
	}

}
