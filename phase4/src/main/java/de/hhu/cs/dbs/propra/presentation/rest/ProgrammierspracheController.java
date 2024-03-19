package de.hhu.cs.dbs.propra.presentation.rest;

import org.glassfish.jersey.media.multipart.FormDataParam;

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

@Path("/programmiersprache")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ProgrammierspracheController {

    @Inject
    private DataSource dataSource;


    @GET
    public int getProgrammierspracheId(@NotNull @QueryParam("name") String name) throws SQLException {
        PreparedStatement preparedStatement;
        List<Map<String, Object>> entities = new ArrayList<>();
        Connection connection = dataSource.getConnection();
        try {
            String sql = "SELECT * FROM programmiersprache WHERE Name = ?;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setObject(1, name);
            preparedStatement.closeOnCompletion();
            ResultSet resultSet = preparedStatement.executeQuery();

            Map<String, Object> entity;

            while (resultSet.next()) {
                entity = new LinkedHashMap<>();
                entity.put("spracheid", resultSet.getObject(1));
                entities.add(entity);
            }
            resultSet.close();

            if(!entities.isEmpty())
                return Integer.parseInt(entities.get(0).get("spracheid").toString());
            else
                return -1;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return -1;
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
    public Response addProgrammiersprache(@NotNull @FormDataParam("benennung") String benennung) throws SQLException {

        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        try {
            String sql = "INSERT INTO programmiersprache(Name) VALUES(?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.closeOnCompletion();
            preparedStatement.setObject(1, benennung);
            preparedStatement.executeUpdate();

            connection.commit();

            return Response.status(Response.Status.CREATED).build();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
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
    @Path("/beherrscht")
    public Response addProgrammierspracheToEntwickler(@NotNull @FormDataParam("kuerzel") String kuerzel,
                                                      @NotNull @FormDataParam("programmierspracheid") int programmierspracheid)
            throws SQLException {

        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);

        try {
            String sql = "INSERT INTO beherrscht(Kuerzel, Programmiersprache_id) VALUES(?,?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.closeOnCompletion();
            preparedStatement.setObject(1, kuerzel);
            preparedStatement.setObject(2, programmierspracheid);
            preparedStatement.executeUpdate();

            connection.commit();

            return Response.status(Response.Status.CREATED).build();
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
}
