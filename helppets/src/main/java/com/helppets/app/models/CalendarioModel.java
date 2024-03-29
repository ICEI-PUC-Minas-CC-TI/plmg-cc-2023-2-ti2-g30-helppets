package com.helppets.app.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.sql.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalendarioModel {
    Integer eventoId;
    String descricao;
    Date data;
    @JsonIgnore
    Integer usuario_usuarioId;
}
