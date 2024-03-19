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

@Path("/bewertungen")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class BewertungController {

    @Inject
    private DataSource dataSource;

    @Context
    private UriInfo uriInfo;


    @PATCH
    @Path("/{bewertungid}")
    @RolesAllowed("KUNDE")
    public Response updateBewertung(@NotNull @PathParam("bewertungid") int bewertungid,
                                    @NotNull @FormDataParam("punktzahl") int punktzahl,
                                    @FormDataParam("text") String kommentar) throws SQLException {

        Connection connection = dataSource.getConnection();
        try {

            //check if the bewertungid is valid
            if (invalidBewertungid(bewertungid, connection))
                return Response.status(Response.Status.NOT_FOUND).entity("message: Ressource existiert nicht").build();


            String sql = "UPDATE bewertung SET Bepunktung=? WHERE ID=?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.closeOnCompletion();
            preparedStatement.setObject(1, punktzahl);
            preparedStatement.setObject(2, bewertungid);
            preparedStatement.executeUpdate();

            //add the comment and map it to the bewertung
            if (kommentar != null && !kommentar.isEmpty()) {

                //check if a text for this bewertung exists
                String sql3 = "SELECT COUNT(*) FROM text WHERE Bewertung_id = ?;";
                PreparedStatement ps2 = connection.prepareStatement(sql3);
                ps2.setObject(1, bewertungid);
                ResultSet rs = ps2.executeQuery();

                //if it does, update it
                if (rs.getInt(1) != 0) {
                    String sql2 = "UPDATE text SET Bewertungstext=? WHERE Bewertung_id=?;";
                    PreparedStatement ps1 = connection.prepareStatement(sql2);
                    ps1.closeOnCompletion();
                    ps1.setObject(1, kommentar);
                    ps1.setObject(2, bewertungid);
                    ps1.executeUpdate();
                } else {
                    //if it doesn't insert the new value
                    String sql2 = "INSERT INTO text(Bewertungstext, Bewertung_id) VALUES(?,?);";
                    PreparedStatement ps1 = connection.prepareStatement(sql2);
                    ps1.closeOnCompletion();
                    ps1.setObject(1, kommentar);
                    ps1.setObject(2, bewertungid);
                    ps1.executeUpdate();
                }
                rs.close();
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

    @DELETE
    @Path("/{bewertungid}")
    @RolesAllowed("KUNDE")
    public Response deleteBewertung(@NotNull @PathParam("bewertungid") int bewertungid) throws SQLException {
        Connection connection = dataSource.getConnection();
        try {

            //check if the bewertungid is valid
            if (invalidBewertungid(bewertungid, connection))
                return Response.status(Response.Status.NOT_FOUND).entity("message: Ressource existiert nicht").build();

            String sql = "DELETE FROM bewertung WHERE ID=?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.closeOnCompletion();
            preparedStatement.setObject(1, bewertungid);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity("message: " + e.getMessage()).build();
        }
        finally {
            try {
                connection.close();
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        }
    }

    private boolean invalidBewertungid(int bewertungid, Connection connection) throws SQLException {
        String sql1 = "SELECT count(*) from bewertung WHERE ID=?;";
        PreparedStatement ps = connection.prepareStatement(sql1);
        ps.setObject(1, bewertungid);
        ResultSet rs = ps.executeQuery();
        int count = rs.getInt(1);
        rs.close();
        return count == 0;
    }

}
