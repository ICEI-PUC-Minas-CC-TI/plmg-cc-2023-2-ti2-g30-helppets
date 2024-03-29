package com.helppets.app.daos;

import com.helppets.app.models.PetsModel;
import com.helppets.app.models.VacinaModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PetsDAO extends DAO {
    private final Logger logger = LoggerFactory.getLogger(PetsDAO.class);

    private VacinaDAO vacinaDAO = new VacinaDAO();

    public PetsDAO() {
        super();
    }

    @Override
    public PetsModel insert(Object object) throws SQLException {
        makeConnection();

        PetsModel pet = (PetsModel) object;

        PreparedStatement statement = returnPreparedStatement("INSERT INTO pets(nome, raca, foto, usuario_usuarioid) values(?, ?, ?, ?) RETURNING *");

        statement.setString(1, pet.getNome());
        statement.setString(2, pet.getRaca());
        statement.setString(3, pet.getFoto());
        statement.setInt(4, pet.getUsuario_usuarioId());

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            pet = parseRowToDto(resultSet);
        }

        closeConnection();

        return pet;
    }

    @Override
    public PetsModel getById(int id) throws SQLException {
        makeConnection();

        ResultSet resultSet = returnStatement().executeQuery("SELECT * FROM pets WHERE petsid=" + id);

        PetsModel pet = null;

        if (resultSet.next()) {
            pet = parseRowToDto(resultSet);
        }

        closeConnection();
        resultSet.close();

        return pet;
    }

    @Override
    public PetsModel deleteById(int id) throws SQLException {
        PetsModel pet = getById(id);

        makeConnection();

        ResultSet resultSet = returnStatement().executeQuery("DELETE FROM pets WHERE petsId=" + id + " RETURNING *");

        if (resultSet.next()) {
            pet = parseRowToDto(resultSet);
        }

        closeConnection();

        return pet;
    }

    @Override
    public List<PetsModel> selectAllWithLimiter(int limiter) throws SQLException {
        makeConnection();

        ResultSet resultSet = returnStatement().executeQuery("SELECT * FROM pets LIMIT "+ limiter);

        ArrayList<PetsModel> pets = new ArrayList<>();

        while (resultSet.next()) {
            pets.add(parseRowToDto(resultSet));
        }

        closeConnection();
        resultSet.close();

        return pets;
    }

    @Override
    public PetsModel updateById(int id, Object object) throws SQLException {
        makeConnection();

        PetsModel pet = (PetsModel) object;

        PreparedStatement statement = returnPreparedStatement("UPDATE pet SET nome=?, raca=?, foto=? WHERE petsid=? RETURNING *");

        statement.setString(1, pet.getNome());
        statement.setString(2, pet.getRaca());
        statement.setString(3, pet.getFoto());
        statement.setInt(4, id);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            pet = parseRowToDto(resultSet);
        }

        return pet;
    }

    @Override
    protected PetsModel parseRowToDto(ResultSet resultSet) throws SQLException {
        PetsModel petsModel = new PetsModel();

        try {
            petsModel.setPetsId(resultSet.getInt("petsid"));
            petsModel.setRaca(resultSet.getString("raca"));
            petsModel.setNome(resultSet.getString("nome"));
            petsModel.setUsuario_usuarioId(resultSet.getInt("usuario_usuarioid"));
            petsModel.setFoto(resultSet.getString("foto"));
        }
        catch (Exception e) {
            logger.error("parseRowToDto - Exception: {}", e.getMessage());
            return null;
        }

        return petsModel;
    }

    public List<PetsModel> selectByUserIdWithLimiter(int userId, int limiter) throws SQLException {
        makeConnection();

        ResultSet resultSet = returnStatement().executeQuery("SELECT * FROM pets where usuario_usuarioid=" + userId + " LIMIT "+ limiter);

        ArrayList<PetsModel> pets = new ArrayList<>();

        while (resultSet.next()) {
            pets.add(parseRowToDto(resultSet));
        }

        closeConnection();
        resultSet.close();

        return pets;
    }

    public PetsModel deleteByIdAndUserId(int userId, int id) throws SQLException {
        PetsModel pet = null;

        makeConnection();

        ArrayList<VacinaModel> deletedVacinas = vacinaDAO.deleteAllVacinasByPetId(id);

        logger.info("deleteByIdAndUserId() - Deleted vacinas: {}", Arrays.toString(deletedVacinas.toArray()));

        ResultSet resultSet = returnStatement().executeQuery("DELETE FROM pets WHERE usuario_usuarioid=" + userId + " AND petsId=" + id + " RETURNING *");

        if (resultSet.next()) {
            pet = parseRowToDto(resultSet);
        }

        closeConnection();

        return pet;
    }
}
