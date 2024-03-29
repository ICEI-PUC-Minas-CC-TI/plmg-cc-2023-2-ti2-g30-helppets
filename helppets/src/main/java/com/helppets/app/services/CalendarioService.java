package com.helppets.app.services;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.helppets.app.daos.CalendarioDAO;
import com.helppets.app.models.CalendarioModel;
import com.helppets.app.utils.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.sql.Date;
import java.util.*;

public class CalendarioService {
    private final CalendarioDAO calendarioDAO = new CalendarioDAO();
    private final Logger logger = LoggerFactory.getLogger(CalendarioService.class);
    private final AuthUtils authUtils = new AuthUtils();
    private final String USER_ID = "usuarioId";

    public CalendarioService() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {}

    public Map<String, Object> insertCalendario(String jwtAuth, Map<String, Object> body) throws SQLException,
            JsonProcessingException, InvalidObjectException, ParseException {
        try {
            if (!authUtils.isJwtValid(jwtAuth)) {
                throw new InvalidObjectException("Invalid jwt");
            }

            DecodedJWT decodedJWT = authUtils.decodeJWT(jwtAuth);
            Map<String, Object> jwtData = authUtils.getPayloadJwt(decodedJWT);

            CalendarioModel calendarioModel = convertMapToCalendarioModel(body, (Integer) jwtData.get(USER_ID));

            calendarioModel = calendarioDAO.insert(calendarioModel);

            Map<String, Object> toReturn = new HashMap<>();

            toReturn.put("event", calendarioModel);

            return toReturn;
        }

        catch (Exception e) {
            logger.error("insertCalendario({}, {}) - Exception: {}", jwtAuth, body, e.getMessage());
            throw e;
        }
    }

    public Map<String, Object> getCalendarioByIdAndUserId(String jwtAuth, int calendarioId) throws InvalidObjectException,
                                                                                                   JsonProcessingException, SQLException {
        try {
            if (!authUtils.isJwtValid(jwtAuth)) {
                throw new InvalidObjectException("Invalid jwt");
            }

            DecodedJWT decodedJWT = authUtils.decodeJWT(jwtAuth);
            Map<String, Object> jwtData = authUtils.getPayloadJwt(decodedJWT);

            CalendarioModel calendarioModel = calendarioDAO.getByIdAndUserId(calendarioId,
                                                                             Integer.parseInt((String) jwtData.get(USER_ID)));

            Map<String, Object> toReturn = new HashMap<>();

            toReturn.put("event", calendarioModel);

            return toReturn;

        }
        catch (Exception e) {
            logger.error("getCalendarioByIdAndUserId({}, {}) - Exception: {}", jwtAuth, calendarioId, e.getMessage());
            throw e;
        }
    }

    public Map<String, Object> getCalendariosByUserId(String jwtAuth, int limit) throws InvalidObjectException,
            JsonProcessingException, SQLException {
        try {
            if (!authUtils.isJwtValid(jwtAuth)) {
                throw new InvalidObjectException("Invalid jwt");
            }

            DecodedJWT decodedJWT = authUtils.decodeJWT(jwtAuth);
            Map<String, Object> jwtData = authUtils.getPayloadJwt(decodedJWT);

            List<CalendarioModel> calendarioModel = calendarioDAO.getByUserId((Integer) jwtData.get(USER_ID),
                                                                              limit);

            Map<String, Object> toReturn = new HashMap<>();

            toReturn.put("event", calendarioModel);

            return toReturn;

        }
        catch (Exception e) {
            logger.error("getCalendarioByIdAndUserId({}, {}) - Exception: {}", jwtAuth, limit, e.getMessage());
            throw e;
        }
    }

    public Map<String, Object> getCalendarioByUserIdAndDay(String jwtAuth, String day, int limit) throws InvalidObjectException,
            JsonProcessingException, SQLException {
        try {
            if (!authUtils.isJwtValid(jwtAuth)) {
                throw new InvalidObjectException("Invalid jwt");
            }

            DecodedJWT decodedJWT = authUtils.decodeJWT(jwtAuth);
            Map<String, Object> jwtData = authUtils.getPayloadJwt(decodedJWT);

            List<CalendarioModel> calendarioModel = calendarioDAO.getByUserIdAndDay((Integer) jwtData.get(USER_ID),
                    day,
                    limit);

            Map<String, Object> toReturn = new HashMap<>();

            toReturn.put("event", calendarioModel);

            return toReturn;

        }
        catch (Exception e) {
            logger.error("getCalendarioByUserIdAndDay({}, {}, {}) - Exception: {}", jwtAuth, day, limit, e.getMessage());
            throw e;
        }
    }

    private CalendarioModel convertMapToCalendarioModel(Map<String, Object> data, Integer usuarioId) throws ParseException {
        CalendarioModel calendarioModel = new CalendarioModel();

        String dataString = (String) data.get("data");
        Date d = new Date(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(dataString).getTime());

        calendarioModel.setData(d);
        calendarioModel.setDescricao((String) data.get("descricao"));
        calendarioModel.setUsuario_usuarioId(usuarioId);

        return calendarioModel;
    }
}
