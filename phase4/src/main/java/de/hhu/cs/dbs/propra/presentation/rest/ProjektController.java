package de.hhu.cs.dbs.propra.presentation.rest;


import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Path("/projekte")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ProjektController {

    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;

    @Context
    private NutzerController nutzerController;


    @GET
    public List<Map<String, Object>> getProjekte() throws SQLException {

        List<Map<String, Object>> entities = new ArrayList<>();
        Connection connection = dataSource.getConnection();
        try {
            String sql = "SELECT * FROM projekt;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.closeOnCompletion();
            ResultSet resultSet = preparedStatement.executeQuery();
            Map<String, Object> entity;

            while (resultSet.next()) {
                entity = new LinkedHashMap<>();
                entity.put("projektid", resultSet.getRow());
                entity.put("name", resultSet.getObject(2));
                entity.put("deadline", resultSet.getObject(3));
                entities.add(entity);
            }
            resultSet.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        //close the connection at the very end
        finally {
            try {
                connection.close();
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        }
        return entities;
    }

    @GET
    @Path("/{projektid}/bewertungen")
    public List<Map<String, Object>> getBewertungen(@NotNull @PathParam("projektid") int projektid) throws SQLException {
        List<Map<String, Object>> entities = new ArrayList<>();
        Connection connection = dataSource.getConnection();
        try {
            String sql = "SELECT bewertung.ROWID, *\n" +
                         "FROM bewertung\n" +
                         "INNER JOIN projekt ON projekt.ID = bewertung.Projekt_id AND Projekt_id=?\n" +
                         "LEFT JOIN text ON text.Bewertung_id = bewertung.id";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setObject(1, projektid);
            preparedStatement.closeOnCompletion();
            ResultSet resultSet = preparedStatement.executeQuery();
            Map<String, Object> entity;

            while (resultSet.next()) {
                entity = new LinkedHashMap<>();
                entity.put("bewertungid", resultSet.getObject(1));
                entity.put("punktzahl", resultSet.getObject(3));

                //if there is no text, instead of printing null print "" as in empty string
                if(resultSet.getObject(12) == null)
                    entity.put("text", "");
                else
                    entity.put("text", resultSet.getObject(12));

                entities.add(entity);
            }
            resultSet.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        //close the connection at the very end
        finally {
            try {
                connection.close();
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        }

        return entities;
    }

    @GET
    @Path("/{projektid}/aufgaben")
    public List<Map<String, Object>> getAufgaben(@NotNull @PathParam("projektid") int projektid) throws SQLException {
        List<Map<String, Object>> entities = new ArrayList<>();
        Connection connection = dataSource.getConnection();
        try {
            String sql = "SELECT aufgabe.ROWID, aufgabe.*\n" +
                         "FROM aufgabe\n" +
                         "INNER JOIN projekt ON projekt.ID = aufgabe.Projekt_id AND aufgabe.Projekt_id=?\n";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setObject(1, projektid);
            preparedStatement.closeOnCompletion();
            ResultSet resultSet = preparedStatement.executeQuery();
            Map<String, Object> entity;

            while (resultSet.next()) {
                entity = new LinkedHashMap<>();
                entity.put("aufgabeid", resultSet.getObject(1));
                entity.put("deadline", resultSet.getObject(4));
                entity.put("beschreibung", resultSet.getObject(6));
                entity.put("status", resultSet.getObject(3));
                entity.put("prioritaet", resultSet.getObject(5));
                entities.add(entity);
            }
            resultSet.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        //close the connection at the very end
        finally {
            try {
                connection.close();
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        }

        return entities;
    }

    @GET
    @Path("/{projektid}/spezialisten")
    public List<Map<String, Object>> getSpezialisten(@NotNull @PathParam("projektid") int projektid) throws SQLException {
        List<Map<String, Object>> entities = new ArrayList<>();
        Connection connection = dataSource.getConnection();
        try {
            String sql = "SELECT aa.ROWID, *\n" +
                         "FROM arbeitet_an aa, nutzer n, spezialist s, projekt p\n" +
                         "WHERE p.ID = aa.Projekt_id AND n.Email=aa.Email AND s.Email=aa.Email AND aa.Projekt_id=?";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setObject(1, projektid);
            preparedStatement.closeOnCompletion();
            ResultSet resultSet = preparedStatement.executeQuery();
            Map<String, Object> entity;

            while (resultSet.next()) {
                entity = new LinkedHashMap<>();
                entity.put("spezialistid", resultSet.getObject(1));
                entity.put("verfuegbarkeitsstatus", resultSet.getObject(7));
                entity.put("email", resultSet.getObject(2));
                entity.put("passwort", resultSet.getObject(5));
                entities.add(entity);
            }
            resultSet.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        //close the connection at the very end
        finally {
            try {
                connection.close();
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        }

        return entities;
    }

    @POST
    @RolesAllowed("KUNDE")
    public Response addProjekt(@NotNull @FormDataParam("name") String benennung,
                               @NotNull @FormDataParam("deadline") String deadline) throws SQLException {

        Connection connection = dataSource.getConnection();
        try {
            String kundeName = securityContext.getUserPrincipal().getName();
            String sql = "INSERT INTO projekt(Projektname, Projektdeadline, Kunde_id, Projektleiter_id) VALUES(?,?,?,?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.closeOnCompletion();
            preparedStatement.setObject(1, benennung);
            preparedStatement.setObject(2, deadline);
            preparedStatement.setObject(3, kundeName);
            preparedStatement.setObject(4, "omar@chouikha.de");
            preparedStatement.executeUpdate();

            return Response.status(Response.Status.CREATED)
                    .header("Location", uriInfo.getAbsolutePathBuilder()
                            .path(getRowId(benennung,connection))).build();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("message: " + e.getMessage()).build();
        }

        //close the connection at the very end
        finally {
            try {
                connection.close();
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        }
    }

    @POST
    @RolesAllowed("KUNDE")
    @Path("/{projektid}/bewertungen")
    public Response addBewertung(@NotNull @PathParam("projektid") int projektid,
                                 @NotNull @FormDataParam("punktzahl") int bepunktung,
                                 @FormDataParam("text") String kommentar) throws SQLException {

        Connection connection = dataSource.getConnection();
        try {

            //non existent projektid detected -> 404 NOT FOUND
            if(getProjekte().stream().noneMatch(e->e.get("projektid").equals(projektid)))
                return Response.status(Response.Status.NOT_FOUND).entity("message: Ressource existiert nicht").build();


            String kundeName = securityContext.getUserPrincipal().getName();
            String sql = "INSERT INTO bewertung(Bepunktung, Projekt_id, Kunde_id) VALUES(?,?,?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.closeOnCompletion();
            preparedStatement.setObject(1, bepunktung);
            preparedStatement.setObject(2, projektid);
            preparedStatement.setObject(3, kundeName);
            preparedStatement.executeUpdate();

            //add the comment and map it to the bewertung
            if(kommentar != null && !kommentar.isEmpty()){
                String sql2 = "INSERT INTO text(Bewertungstext, Bewertung_id) VALUES(?,?);";
                String getRowId= "SELECT last_insert_rowid();";
                PreparedStatement ps = connection.prepareStatement(getRowId);
                ps.closeOnCompletion();
                ResultSet resultSet = ps.executeQuery();
                int bewertung_id = resultSet.getInt(1);
                resultSet.close();
                PreparedStatement ps1 = connection.prepareStatement(sql2);
                ps1.closeOnCompletion();
                ps1.setObject(1, kommentar);
                ps1.setObject(2, bewertung_id);
                ps1.executeUpdate();
            }

            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("message: " + e.getMessage()).build();
        }

        //close the connection at the very end
        finally {
            try {
                connection.close();
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        }
    }

    @POST
    @Path("/{projektid}/aufgaben")
    @RolesAllowed("PROJEKTLEITER")
    public Response addAufgabe(@NotNull @PathParam("projektid") int projektid,
                               @NotNull @FormDataParam("deadline") String deadline,
                               @NotNull @FormDataParam("beschreibung") String beschreibung,
                               @NotNull @FormDataParam("status") String status,
                               @NotNull @FormDataParam("prioritaet") String prioritaet) throws SQLException {

        Connection connection = dataSource.getConnection();
        try {

            //non existent projektid detected -> 404 NOT FOUND
            if(getProjekte().stream().noneMatch(e->e.get("projektid").equals(projektid)))
                return Response.status(Response.Status.NOT_FOUND).entity("message: Ressource existiert nicht").build();


            String sql = "INSERT INTO aufgabe(Status, Deadline, Prioritaet, Beschreibung, Projekt_id) VALUES(?,?,?,?,?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.closeOnCompletion();
            preparedStatement.setObject(1, status);
            preparedStatement.setObject(2, deadline);
            preparedStatement.setObject(3, prioritaet);
            preparedStatement.setObject(4, beschreibung);
            preparedStatement.setObject(5, projektid);
            preparedStatement.executeUpdate();
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("message: " + e.getMessage()).build();
        }

        //close the connection at the very end
        finally {
            try {
                connection.close();
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        }
    }

    @POST
    @RolesAllowed("PROJEKTLEITER")
    @Path("/{projektid}/spezialisten")
    public Response addSpezialist(@NotNull @PathParam("projektid") int projektid,
                                  @NotNull @FormDataParam("spezialistid") int spezialistid) throws SQLException {

        Connection connection = dataSource.getConnection();
        try {

            //non existent projektid detected -> 404 NOT FOUND
            if(getProjekte().stream().noneMatch(e->e.get("projektid").equals(projektid)))
                return Response.status(Response.Status.NOT_FOUND).entity("message: Ressource existiert nicht").build();

            String sql = "INSERT INTO arbeitet_an(Email, Projekt_id) VALUES(?,?);";
            String sql1 = "SELECT Email FROM spezialist WHERE rowid LIKE ?;";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.closeOnCompletion();

            PreparedStatement ps = connection.prepareStatement(sql1);
            ps.setObject(1, spezialistid);
            ps.closeOnCompletion();

            ResultSet resultSet = ps.executeQuery();

            //get the email of the spezialist, if it's null -> 400 BAD REQUEST
            Map<String, Object> entity = new LinkedHashMap<>();
            while (resultSet.next()) {
                entity.put("email", resultSet.getObject(1));
            }
            resultSet.close();

            preparedStatement.setObject(1, entity.get("email"));
            preparedStatement.setObject(2, projektid);
            preparedStatement.executeUpdate();
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("message: " + e.getMessage()).build();
        }

        //close the connection at the very end
        finally {
            try {
                connection.close();
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        }
    }


    //Determines the rowid of a given projektname
    private String getRowId(String name, Connection connection) throws SQLException {
        PreparedStatement ps =
                connection.prepareStatement("SELECT rowid FROM projekt WHERE LOWER(Projektname) LIKE LOWER(?)");
        ps.setObject(1, name);
        ps.closeOnCompletion();
        ResultSet rs = ps.executeQuery();
        String rowid = rs.getString(1);
        rs.close();
        return rowid;
    }

}
