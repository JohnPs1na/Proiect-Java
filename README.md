# Project - Java
# eTicketing

Some Options:
1) Register + Login
2) As an artist I can publish an event and tickets to the event
3) As a client i can fill my balance
4) As a client i can view my balance
5) As a client i can purchase tickets
6) As a user i can search for specific tickets to specific events
7) I can create a client/artist
8) I can update a client/artist/event/ticket
9) I can delete a client/artist/event/ticket
10.I can view Information about clients/artists/events/tickets

# Implemented Classes
○ Person 
  - Client + ClientService
  - Artist + ArtistService
  - PersonFactory
  
○ Event
  - EventFactory + EventService
  
○ Ticket
  - TicketFactory + TicketService
  
○ User

Aditional:
  - @FunctionalInterface - Service_Function
  - Option_Function struct (that is used to instantiate an array of operations(Strings) with lambda functions)
  - runMenu (which is the skelet for the menus, almost all services extend this class)
  - AuditService 

○ Main + MainService
