import java.util.*;
import java.text.SimpleDateFormat;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.math.BigDecimal;
import java.math.RoundingMode;



public class JavaTickets {
    public static void main(String[] args) {

    	// Ticket класс для хранения данных о времени отправления, прибытия, времени в пути по каждому рейсу    	
    	// ArrayList<Ticket> массив данных по всем рейсам
    	// ArrayList<String> listKeys массив ключевых слов для получения данных из файла
    	// fillArrayListTicket функция заполняющая массив ArrayList<Ticket> данными из файла tickets.json
    	// fillArrayListTicketFile функция заполняющая массив ArrayList<Ticket> данными из файла <namefile>
    	// getAvgTimeFromListBigDecimal(ArrayList<Ticket>) функция получения среднего значения времени в пути из массива
    	// getPercentil(ArrayList<Ticket>, Percentil) функция получения значения  заданного перцентиля в массиве
    	
    		if(args.length == 0) {
    			System.out.println("Usage: java Tickets <namefile>");
//    			return;
    		}
    			
    	
        	ArrayList<String> listKeys = new ArrayList<String>();
        	listKeys.add("departure_time");
        	listKeys.add("departure_date");
        	listKeys.add("arrival_date");
        	listKeys.add("arrival_time");

        	ArrayList<Ticket> listTickets = new ArrayList<Ticket>();

        	fillArrayListTicket(listTickets, listKeys);
//       	fillArrayListTicketFile(listTickets, listKeys, args[0]);        	

        	System.out.println("Среднее время в пути : " + getAvgTimeFromListBigDecimal(listTickets));
        	
        	Collections.sort(listTickets, Ticket.minTimeInWayBigDecimalComparator);
        	System.out.println("Значение 90-го перцентиля : " + getPercentil(listTickets, new BigDecimal(0.9)));
        	System.out.println("Значение 30-го перцентиля : " + getPercentil(listTickets, new BigDecimal(0.3)));
        	System.out.println("Значение 20-го перцентиля : " + getPercentil(listTickets, new BigDecimal(0.2)));        	
        	System.out.println("Значение 100-го перцентиля : " + getPercentil(listTickets, new BigDecimal(1.0)));        	

    }

  //public static void fillArrayListTicketFile функция заполняющая массив ArrayList<Ticket> данными из файла <namefile>
    
    public static void fillArrayListTicketFile(ArrayList<Ticket> listTickets, ArrayList<String> listKeys, String strNameFile) {
   	// ArrayList<String> listKeyValues массив для инициализации нового значения Ticket в списке ArrayList<Ticket> listTickets
    	ArrayList<String> listKeyValues = new ArrayList<String>();
		try(BufferedReader br = new BufferedReader(new FileReader(strNameFile)))
		{
				String s;
				while((s = br.readLine()) != null ) {
					String[] words = s.split("[:,\"]");
					for(String word : words){
						if(listKeys.indexOf(word) != -1) {
	// В текущей строке s найдено ключевое слово из listKeys
					    	listKeyValues.add(getValueKeyword(s, word));
   	// В список listKeyValues добавлено значение ключевого слова из listKeys
					    	if(word.indexOf("arrival_time") != -1){
	// "arrival_time" - последнее ключевое слово. После получения его значения
	// добавляем новый объект Ticket в массив ArrayList<Ticket> listTicket с помощью конструктора Ticket(ArrayList<String>)				    		
					    		listTickets.add(new Ticket(listKeyValues));
					    		listKeyValues.clear();
					    	}
					    	
					    }
					    
					}
				}
		}catch(IOException ex){
			System.out.println(ex.getMessage());
        } 
    	
    }
    

	// public static BigDecimal getPercentil(ArrayList<Ticket>, Percentil) функция получения значения  заданного перцентиля в массиве
    
    public static BigDecimal getPercentil(ArrayList<Ticket> listTickets, BigDecimal bdPercentil ) {
    	// BigDecimal bdResult 
    	// BigDecimal bdPart - дробная часть интервала в расчете перцентиля
    	// BigDecimal bdFloor -   целая часть интервала в расчете перцентиля
    	// int iCountInterval - количество интервалов в расчете перцентиля
    	// BigDecimal bdIntervalStart - нижняя граница ближайшего интервала   	
    	// BigDecimal bdIntervalStart - верхняя граница ближайшего интервала   	
    	// BigDecimal bdCountInverval - интервал, на который приходится рассчитываемый перцентиль    	
    	// Получить количество интервалов n
    	// Определить ширину интервала 1/(n-1)
    	// Определить количество интервалов в нужном перцентиле. Пример 90-процентиль: для 10 значений 0.9/(1/(10-1))=0.9/(1/9)=0.81
    	// Получаем 8 целых интервалов. Низ восьмого интервала + 0.1 * (разница значений границ 9-го интервала)
    	    
    	// 20-процентиль для 10 значений 0.2*/(1/9)=0.2*9 = 1.8
    	// Низ 2-го интервала +  0.8 * (разница значений границ 2-го интервала) 13 + 0.8*(13.5-13.0) =13+0.8*0.5= 13.4
    	
    	
    	BigDecimal bdResult = new BigDecimal(-1.).setScale(3, RoundingMode.HALF_EVEN);
    	BigDecimal bdPart = new BigDecimal(0.).setScale(3, RoundingMode.HALF_EVEN);
    	BigDecimal bdFloor = new BigDecimal(1.).setScale(3, RoundingMode.HALF_EVEN);
    	BigDecimal bdCountInterval = new BigDecimal(1.).setScale(3, RoundingMode.HALF_EVEN);
    	int iCountInterval = 1;
    	iCountInterval = listTickets.size() - 1;
    	if(iCountInterval <= 0)
    				return bdResult;
    	
    	bdCountInterval = bdPercentil.multiply(new BigDecimal(1.* iCountInterval).setScale(3, RoundingMode.HALF_EVEN)).setScale(3, RoundingMode.HALF_EVEN);
    	bdFloor = bdCountInterval.setScale(0, RoundingMode.FLOOR);
    	bdPart =  bdCountInterval.subtract(bdFloor.setScale(3, RoundingMode.HALF_EVEN)).setScale(3, RoundingMode.HALF_EVEN);
    	
    	if(bdPart.compareTo(new BigDecimal(0.).setScale(3, RoundingMode.HALF_EVEN)) <= 0) {
    		bdResult = new BigDecimal(listTickets.get(bdFloor.intValue()).minTimeInWayBigDecimal.doubleValue()).setScale(3, RoundingMode.HALF_EVEN);
        	return bdResult.setScale(2, RoundingMode.HALF_EVEN);
    		
    	}
    	
    	
    	BigDecimal bdIntervalStart = new BigDecimal(listTickets.get(bdFloor.intValue()).minTimeInWayBigDecimal.doubleValue()).setScale(3, RoundingMode.FLOOR);
    	BigDecimal bdIntervalStop = new BigDecimal(listTickets.get(Math.min(bdFloor.intValue() + 1,listTickets.size()-1)).minTimeInWayBigDecimal.doubleValue()).setScale(3, RoundingMode.FLOOR);
    	
    	bdPart = bdIntervalStop.subtract(bdIntervalStart).multiply(bdPart).setScale(3, RoundingMode.HALF_EVEN);
    	bdResult = bdIntervalStart.add(bdPart).setScale(3, RoundingMode.HALF_EVEN);
    	
    	return bdResult.setScale(2, RoundingMode.HALF_EVEN);
    	
    }

//public static BigDecimal getAvgTimeFromListBigDecimal(ArrayList<Ticket>) функция получения среднего значения времени в пути из массива
    
    
    public static BigDecimal getAvgTimeFromListBigDecimal(ArrayList<Ticket> listTickets) {
    	BigDecimal dResult = new BigDecimal(0.);
    	
    	dResult.setScale(3, RoundingMode.HALF_EVEN);
    	int count = 0;
    	for(Ticket ticketCurrent : listTickets) {
    		dResult = dResult.add(ticketCurrent.minTimeInWayBigDecimal);
    		count++;
    	}
    	
    	return dResult.divide(new BigDecimal(count)).setScale(2, RoundingMode.HALF_EVEN);
    }
    
    
	//public static void fillArrayListTicket функция заполняющая массив ArrayList<Ticket> данными из файла tickets.json
    
    public static void fillArrayListTicket(ArrayList<Ticket> listTickets, ArrayList<String> listKeys) {
    	ArrayList<String> listKeyValues = new ArrayList<String>();
		try(BufferedReader br = new BufferedReader(new FileReader("/home/inet/Java/JavaTest/tickets.json")))
		{
				String s;
				while((s = br.readLine()) != null ) {
					String[] words = s.split("[:,\"]");
					for(String word : words){
					    if(listKeys.indexOf(word) != -1) {
					    	listKeyValues.add(getValueKeyword(s, word));
					    	if(word.indexOf("arrival_time") != -1){
					    		listTickets.add(new Ticket(listKeyValues));
					    		listKeyValues.clear();
					    	}
					    	
					    }
					    
					}
				}
		}catch(IOException ex){
			System.out.println(ex.getMessage());
        } 
    	
    }
    

    // public static String getValueKeyword(String strWord, String strKeyword)
    // функция получения значения для ключа strKeyWord из строки strWord
    
    public static String getValueKeyword(String strWord, String strKeyword) {
    	String strResult = "NotFound";
    	String strWork;
    // Разделитель между минутами ":". Ищем первое вхождение ":" , которое разделяет ключ и значение. 
  	// Меняем следующее вхождение ":" на "|" 	
    	if(strWord.indexOf(":") != 0) {
    		 strWork = strWord.substring(0,strWord.indexOf(":")-1) + "|" + strWord.substring(strWord.indexOf(":")+1);
    	}
    	else {
    			strWork = strWord;
    	}
    	String[] words = strWork.split("[|,\"]");
    // boolean bFlag индикатор нахождения ключевого слова (ключа)
    	boolean bFlag = false;
    	for(String word : words){
    		if(bFlag && word.trim().length() > 0)
    // bFlag установлен. Возвращаем значение ключа
    			return word;
    	    if (strKeyword.indexOf(word) != -1) {
    // Найдем ключ. Следующее слово значение	    	
    	    	bFlag = true  ;
    	    };
    	}
    	return strResult;
    }
    

}

    
class Ticket implements Comparable<Ticket>{
// 	String dateDeparture дата отправления
	String dateDeparture;
// 	String timeDeparture время отправления
	String timeDeparture;
// 	String dateArrival дата прибытия
	String dateArrival;
// 	String timeArrival время прибытия
	String timeArrival;
	
	double minTimeInWay;
// 	BigDecimal minTimeInWayBigDecimal время в пути в минутах
	BigDecimal minTimeInWayBigDecimal;


//	BigDecimal Percentil = new BigDecimal(0).setScale(3, RoundingMode.HALF_EVEN);
//	float kFloat = 1;
//конструктор на основе массива строк с ключевыми значениями	
	Ticket(ArrayList<String> arr){
		this.dateDeparture = arr.get(0);
		if(arr.get(1).length() < 5)
			this.timeDeparture = "0" + arr.get(1);
		else
			this.timeDeparture = arr.get(1);
		this.dateArrival = arr.get(2);
		if(arr.get(3).length() < 5)
			this.timeArrival = "0" + arr.get(3);
		else
			this.timeArrival = arr.get(3);
		
		Calendar dateDeparture = Calendar.getInstance();
		Calendar dateArrival = Calendar.getInstance();
    	try {
    			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm Z");
    		    // Время отправления из Владивостока часовой пояс GMT+10    			
    			dateDeparture.setTime(sdf.parse(this.dateDeparture + " " + this.timeDeparture + " +1000"));
    		    // Время прибытия в Тель-Авива часовой пояс GMT+3    			
    			dateArrival.setTime(sdf.parse(this.dateArrival + " " + this.timeArrival + " +0300"));
    	} 
		catch (ParseException e) {
    		  e.printStackTrace();
    		}
		
		this.minTimeInWay = dateArrival.getTimeInMillis()/(1000*60.0) - dateDeparture.getTimeInMillis()/(1000*60.0);
    	BigDecimal bigDecimal = new BigDecimal(this.minTimeInWay);
	    this.minTimeInWayBigDecimal = bigDecimal.setScale(3, RoundingMode.HALF_EVEN);
		
	}

	// метод для сравнения значений класса Ticket по свойству minTimeInWayBigDecimal
	public int compareTo(Ticket ticket) {
        return this.minTimeInWayBigDecimal.compareTo(ticket.minTimeInWayBigDecimal);
    }
	
   public String toString() {
	        return "[  DateTimeDeparture= " + this.dateDeparture + " " + this.timeDeparture +
	        				", DateTimeArrival= " + this.dateArrival + " " + this.timeArrival + "timeInWay = " + this.minTimeInWayBigDecimal + "]";
    }	

   // метод компаратор для сортировки
   public static Comparator<Ticket> minTimeInWayBigDecimalComparator = new Comparator<Ticket>() {
	   	public int compare(Ticket e1, Ticket e2) {
            return (int) e1.minTimeInWayBigDecimal.compareTo(e2.minTimeInWayBigDecimal);
        }
    };
 	
}


