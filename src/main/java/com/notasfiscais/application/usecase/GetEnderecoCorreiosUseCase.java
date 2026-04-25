package com.notasfiscais.application.usecase;

import com.notasfiscais.application.dto.EnderecoCorreiosDTO;
import com.notasfiscais.application.exceptions.CepNaoEncontradoException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class GetEnderecoCorreiosUseCase {

    private static final String VIA_CEP_URL = "https://viacep.com.br/ws/";

    public EnderecoCorreiosDTO execute(String nrCep) throws Exception {
        String cepSemMascara = nrCep.replaceAll("[^\\d]", "");

        if (cepSemMascara.length() != 8) {
            throw new CepNaoEncontradoException("CEP inválido: " + nrCep);
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(VIA_CEP_URL + cepSemMascara + "/json/"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body());

            if (root.has("erro")) {
                throw new CepNaoEncontradoException("Nenhum endereço encontrado para o CEP: " + nrCep);
            }

            return mapper.treeToValue(root, EnderecoCorreiosDTO.class);
        } catch (CepNaoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            throw new Exception("Erro ao buscar endereço para o CEP informado");
        }
    }
}
