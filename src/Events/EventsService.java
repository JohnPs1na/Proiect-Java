package events;

import tickets.TicketService;
import option_function.OptionFunction;
import option_function.RunMenu;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class EventsService extends RunMenu {

    private static EventsService eventsService_instance = null;
    private final HashMap<Integer,Event> eventList = new HashMap<>();
    private final EventFactory eventFactory = new EventFactory();

    private EventsService(){
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from events");
            while(resultSet.next()){
                Event e = eventFactory.createEvent(resultSet);
                eventList.put(e.getEventId(),e);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        operations.add(
                new OptionFunction(
                        "Publish Event",
                        ()-> {
                            if (Objects.equals(user.getLabel(), "artist")) {
                                Scanner in = new Scanner(System.in);
                                Event e = eventFactory.createEvent(in);
                                System.out.println("Introduce cost for your ticket: ");
                                String cost = in.nextLine();
                                eventList.put(e.getEventId(),e);
                                create(e);
                                createAnnouncement(e.getEventId(),user.getArtistId());
                                TicketService ticketService = TicketService.getInstance();
                                ticketService.createDefaultTicket(e.getEventId(), cost,e.getTitle(),e.getStartTime());
                            }
                            else {
                                System.out.println("Clients dont have permision to publish an event");
                            }
                        }));

        operations.add(new OptionFunction(
                "Update Event",
                ()-> {
                    for(Event event : eventList.values()){
                        System.out.println(event);
                    }
                    System.out.println("Insert the id of the event you want to update");
                    Scanner in = new Scanner(System.in);
                    int id = Integer.parseInt(in.nextLine());
                    updateEvent(id);
                }));

        operations.add(new OptionFunction(
                "Delete an event By id",
                ()-> {
                    if (Objects.equals(user.getLabel(), "artist")) {

                        for(Event event : eventList.values()){
                            System.out.println(event);
                        }

                        Scanner in = new Scanner(System.in);
                        System.out.println("Insert the id of the event you want to remove");
                        int id = Integer.parseInt(in.nextLine());
                        TicketService ticketService = TicketService.getInstance();
                        ticketService.deleteTicketByEventId(id);
                        deleteAnnouncementByEventId(id);
                        deleteById(id);
                        for(Event e : eventList.values()){
                            if (e.getEventId() == id){
                                eventList.remove(e.getEventId());
                            }
                        }
                        System.out.println("Event with id 6 deleted");
                    } else {
                        System.out.println("Clients dont have permision to delete events");
                    }
                }));
        operations.add(new OptionFunction(
                "Get All Events",
                ()-> {
                    for(Event event : eventList.values()){
                        System.out.println(event);
                    }
                }));
    }
    public static EventsService getInstance(){
        if (eventsService_instance == null){
            eventsService_instance = new EventsService();
        }
        return eventsService_instance;
    }

    public Event getEventByIdFromHashMap(int eventId){
        return eventList.get(eventId);
    }

    public void create(Event event){
        try{
            String q = "Insert into events (eventId,ticketNum,title,genre,status,startTime,location) values (?,?,?,?,?,?,?)";
            PreparedStatement stmt = connection.prepareStatement(q);
            stmt.setInt(1,event.getEventId());
            stmt.setInt(2,event.getTicketNum());
            stmt.setString(3,event.getTitle());
            stmt.setString(4,event.getGenre());
            stmt.setString(5,event.getStatus());
            stmt.setString(6,event.getStartTime());
            stmt.setString(7,event.getLocation());
            stmt.executeUpdate();
            stmt.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateEvent(int eventId){
        try{
            Scanner in = new Scanner(System.in);
            while (true) {
                System.out.println("Insert the column you want to alter (type close to exit): ");
                String col = in.nextLine();
                if (Objects.equals(col, "close")){
                    break;
                }

                System.out.println("Insert new value for your column");
                String val = in.nextLine();

                String q = "Update events set "+col+"=? where eventId = ?";
                PreparedStatement stmt = connection.prepareStatement(q);
                stmt.setString(1,val);
                stmt.setInt(2,eventId);
                stmt.executeUpdate();
                stmt.close();
                updateEventList();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateEventList(){
        try {
            eventList.clear();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from events");
            while(resultSet.next()){
                Event e = eventFactory.createEvent(resultSet);
                eventList.put(e.getEventId(),e);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void createAnnouncement(int eventId,int artistId){
        try{
            String q = "Insert into announcements (artistId,eventId) values (?,?)";
            PreparedStatement stmt = connection.prepareStatement(q);
            stmt.setInt(1,artistId);
            stmt.setInt(2,eventId);
            stmt.executeUpdate();
            stmt.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteById(int id){
        try{

            String q = "Delete from events where eventId = ?";
            PreparedStatement stmt = connection.prepareStatement(q);
            stmt.setInt(1,id);
            stmt.executeUpdate();
            stmt.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void delete(Event event){
        try{
            String q = "Delete from events where eventId = ?;";
            PreparedStatement stmt = connection.prepareStatement(q);
            stmt.setInt(1,event.getEventId());
            stmt.executeUpdate();
            stmt.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteAnnouncementByEventId(int eventId){
        try {
            String q = "Delete from announcements where eventId = ?";
            PreparedStatement stmt = connection.prepareStatement(q);
            stmt.setInt(1,eventId);
            stmt.executeUpdate();
            stmt.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateEventTicketNum(int newTicketNum,int eventId){
        try{
            int actualNum = 0;
            for(Event event : eventList.values()){
                if (eventId == event.getEventId()){
                    event.setTicketNum(event.getTicketNum() - newTicketNum);
                    actualNum = event.getTicketNum();
                }
            }

            String q = "Update events set ticketNum =  ? where eventId = ?";
            PreparedStatement stmt = connection.prepareStatement(q);
            stmt.setInt(1,actualNum);
            stmt.setInt(2,eventId);
            stmt.executeUpdate();
            stmt.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
