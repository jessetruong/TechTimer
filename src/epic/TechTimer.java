package epic;

import java.awt.event.KeyListener;
import java.awt.Font;
import java.util.*;
import java.io.*;
import java.time.*;

import javax.sound.sampled.*;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.xml.datatype.Duration;


public class TechTimer {	
	
	public String[][] createTimeTable(){
		String piece = "";
		String time;
		String[][] timeTable = new String[0][2];
		int count = 0;
		
		Scanner user = new Scanner(System.in);
		
		while(!piece.equals("END")){
			String[][] table = new String[timeTable.length+1][2];
			System.arraycopy(timeTable, 0, table, 0, timeTable.length);
			
			System.out.print("Enter piece name: ");
			piece = user.next(); 
			
			table[count][0] = piece;
			
			if(piece.equals("END"))
				table[count][1] = "0";
			else{
				System.out.print("Enter minutes for tech: ");
				time = user.next(); 
				table[count][1] = time;
			}
			count++;
			
			timeTable = table;		
			printTimeTable(timeTable);
		}
		
		return timeTable;
	}
	
	public void printTimeTable(String[][] table){
		if (table[0][0] != null){
			for(int i=0; i<table.length; i++){
				if(table[i][0].equals("END"))
					System.out.println("END OF SHOW");
				else
					System.out.println("Piece " + (i+1) + ": " + table[i][0] + " -- " + table[i][1] + " minutes");
			}
		}else{
			System.out.println("Empty Table");
		}
		
		System.out.println();
	}
	
	public String toTimeFormat(int seconds){
		int s = seconds;
		String hour = Integer.toString((int)Math.floor(s/3600));
		s = s - Integer.parseInt(hour)*3600;
		String minute = Integer.toString((int)Math.floor(s/60));
		if(minute.length() == 1)
			minute = "0" + minute;
		s = seconds - Integer.parseInt(hour)*3600 - Integer.parseInt(minute)*60;
		String secs = Integer.toString(s);
		if(secs.length() == 1)
			secs = "0" + secs;
		String time = hour + ":" + minute + ":" + secs;
		
		return time;
	}
	
	public int printSchedule(int st, String[][] t){
		//Given the start time, print out the schedule
		int runtime = 0;
		System.out.println("SCHEDULE FOR TODAY");
		if (t[0][0] != null){
			for(int i=0; i<t.length; i++){
				if(t[i][0].equals("END"))
					System.out.println(toTimeFormat(st + runtime) + " -- END OF SHOW");
				else{
					System.out.println(toTimeFormat(st + runtime) +" -- " + t[i][0]);
					runtime = runtime + Integer.parseInt(t[i][1])*60;
				}
			}
		}else{
			System.out.println("Empty Table");
		}
		
		runtime = runtime/60;
		System.out.println("Runtime " + runtime + " minutes");
		System.out.println();
		return runtime;
	}
	
	public void countdown(String[][] t){
		 Calendar calendar = new GregorianCalendar();
		 
		 JFrame frame = new JFrame();
        frame.setSize(1200,700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        JLabel label1 = new JLabel("TECH TIMER", SwingConstants.CENTER);
        label1.setFont(new Font("Serif", Font.PLAIN, 150));
        frame.add(label1);
        
        KeyListener s;
        
        File starter = new File("src/Start.wav");
        File twentyLeft = new File("src/20.wav");
        File fifteenLeft = new File("src/15.wav");
        File tenLeft = new File("src/10.wav");
        File fiveLeft = new File("src/5.wav");
        File twoLeft = new File("src/2.wav");
        File thirty = new File("src/30seconds.wav");
        File chopped = new File("src/Chopped.wav");
        File tenSecondsLeft = null;
		 
		 int startTime = calendar.get(Calendar.HOUR_OF_DAY)*3600 + calendar.get(Calendar.MINUTE)*60 + calendar.get(Calendar.SECOND);
		  
		 int runtime = printSchedule(startTime, t);
		 int endTime = startTime + runtime*60;
		 
		 int currentTime = startTime;
		 int counter = 0;
		 int nextTime = startTime + Integer.parseInt(t[counter][1])*60;
		 boolean announced = true;
		 int timeAnnounced = calendar.get(Calendar.SECOND);
		 File announcement = starter;
		 
		 while (currentTime < endTime){
			 Calendar update = new GregorianCalendar();
			 currentTime = (update.get(Calendar.HOUR_OF_DAY)*3600 + update.get(Calendar.MINUTE)*60 + update.get(Calendar.SECOND));
			
			if(announced){
				//wait until a 30 seconds has passed to reset it
				int timeToWait = 0;
				timeToWait = 30;
				
				if(Math.abs(timeAnnounced-update.get(Calendar.SECOND))>timeToWait){
					announced = false;
					System.out.println("ANNOUNCED SWITCHED TO FALSE");
				}
			}
			
			int timeLeft = (int)Math.ceil((nextTime - currentTime)/60);

			if(announcement != null){
				while(announcement != null && announcement.equals(starter)){
					update = new GregorianCalendar();
					if( Math.abs(timeAnnounced-update.get(Calendar.SECOND))>5){
						announced = false;
						System.out.println("ANNOUNCED SWITCHED TO FALSE");
					}
					
					if(!announced){
						try {
				            AudioInputStream audio = AudioSystem.getAudioInputStream(announcement);
				            Clip clip = AudioSystem.getClip();
				            clip.open(audio);
				            clip.start();
				        }
				        
				        catch(UnsupportedAudioFileException uae) {
				            System.out.println(uae);
				        }
				        catch(IOException ioe) {
				            System.out.println(ioe);
				        }
				        catch(LineUnavailableException lua) {
				            System.out.println(lua);
				        }
						
						announced = true;
						timeAnnounced = update.get(Calendar.SECOND);
						
						
						announcement = null;
						
						System.out.println("Starting " + t[counter][0] + "'s piece - " + timeLeft + " minutes.");
					}
				}
			}
			 
			switch(timeLeft){
				case 20:	
					announcement = twentyLeft;
					break;
				case 15:
					announcement = fifteenLeft;
					break;
				case 10:
					announcement = tenLeft;
					break;
				case 5:
					announcement = fiveLeft;
					break;
				case 2:
					announcement = twoLeft;
					break;
				case 0:
					announcement = chopped;
					break;	
				default:
					announcement = null;
					break;
			}
			
			if(timeLeft == 0 && (currentTime+30) >= nextTime-timeLeft*60)
				label1.setText((30-((currentTime+30)-(nextTime-timeLeft*60))) + " seconds left");
			else if(timeLeft == 0)
				label1.setText((timeLeft+1) + " minute left");
			else
				label1.setText((timeLeft+1) + " minutes left");
			frame.add(label1);
			frame.repaint();
			
			
			if ((currentTime+31) == nextTime-timeLeft*60 && timeLeft == 0){
				announcement = thirty;
			}
				
					
			if(announcement != null && ((currentTime == nextTime-timeLeft*60 && (timeLeft%5 == 0 | timeLeft == 2))	 | announcement.equals(thirty)) && !announced ){
				try {
		            AudioInputStream audio = AudioSystem.getAudioInputStream(announcement);
		            Clip clip = AudioSystem.getClip();
		            clip.open(audio);
		            clip.start();
		        }
		        
		        catch(UnsupportedAudioFileException uae) {
		            System.out.println(uae);
		        }
		        catch(IOException ioe) {
		            System.out.println(ioe);
		        }
		        catch(LineUnavailableException lua) {
		            System.out.println(lua);
		        }
				
				
				announced = true;

				timeAnnounced = update.get(Calendar.SECOND);
				
				if(timeLeft == 0 && !announcement.equals(thirty)){
					counter++;
					nextTime = currentTime + Integer.parseInt(t[counter][1])*60;
					announcement = starter;
				}
				
				if(announcement.equals(thirty))
					announcement = chopped;
				
				
				
				System.out.println("Time remaining for " + t[counter][0] + "'s piece - " + timeLeft + " minutes.");
				
				System.out.println("Current time " + toTimeFormat(currentTime) + " / End time " + toTimeFormat(endTime));
			}
			 
		}
		 
		 System.out.println("SHOW IS OVER");
		 label1.setText("SHOW OVER");
		frame.add(label1);
		frame.repaint();
		
	}

	public static void main(String[] args){
		// TODO Auto-generated method stub
		System.out.println("Hello World!");
		TechTimer tech = new TechTimer();
		String[][] table = tech.createTimeTable();
		
		tech.countdown(table);

		
	}

}
