package MyPackage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
    public MyServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try  {
		        //API Setup
				String apiKey = "f3deb01c2727e4802a418ba031f06a08";
				
				//Get the city from the form input
				String city = request.getParameter("city");
				System.out.println(city);
				
				//Create the URL for the OpenWeatherMap API request
				String apiURL = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;
				apiURL = apiURL.replace(" ", "%20");
				
				System.out.println(apiURL);

				URL url = new URL(apiURL);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				//connection.connect();
				connection.setRequestMethod("GET");
				
				//Read the data from network
				InputStream inputStream = connection.getInputStream();
				InputStreamReader reader = new InputStreamReader(inputStream);
				//System.out.println(reader);
				
				//want to store in spring
				StringBuilder responseContent = new StringBuilder();
				
				//Input lene ke liye from the reader, will create scanner object
				Scanner scanner = new Scanner(reader);
				
				while(scanner.hasNext()) {
					responseContent.append(scanner.nextLine());
				}
				
				scanner.close();
				//System.out.println(responseContent);
				
				//Typecasting = Parsing the data into JSON
				Gson gson = new Gson();
				JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);
		        //System.out.println(jsonObject);
		        
		        //Date & time
		      //Date & Time. here typecasting of date object to string
                long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
               // System.out.println(dateTimestamp);
                String date = new Date(dateTimestamp).toString() ;
                //System.out.println(date);

                
                //Temperature
                double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
                int temperatureCelsius = (int) (temperatureKelvin - 273.15);
               
                //Humidity
                int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
                
                //Wind Speed
                double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
                
                //Weather Condition
                String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString() ;
		        
                //Set the data as request attribute (for sending to the jsp page)
                request.setAttribute("date", date);
                request.setAttribute("city", city);
                request.setAttribute("temperature", temperatureCelsius);
                request.setAttribute("humidity", humidity);
                request.setAttribute("windSpeed", windSpeed);
                request.setAttribute("weatherCondition", weatherCondition);
                request.setAttribute("weatherData", responseContent);
                //close the connection
                connection.disconnect();
                
               request.getRequestDispatcher("index.jsp").forward(request, response) ;
               
               
		        }catch(IOException e) {
		    	  e.printStackTrace();
		      }
					
			}

	}


