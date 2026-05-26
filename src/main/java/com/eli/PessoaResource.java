package com.eli;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("pessoa")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PessoaResource {

    @GET
    public List<Pessoa> getPessoas() {
        return Pessoa.listAll();
    }
    @GET
    @Path("/ano")
    public List<Pessoa> getPessoa(@QueryParam("anoNascimento") Integer anoNascimento) {
        if (anoNascimento != null) {
            return Pessoa.list("anoNascimento", anoNascimento);
        }

        return Pessoa.listAll();
    }

    @POST
    @Transactional
    public Pessoa createPessoa(Pessoa pessoa) {
        pessoa.id = null;
        pessoa.persist();
        return pessoa;
    }
    @PUT
    @Transactional
    public Pessoa updatePessoa(Pessoa pessoa) {
        Pessoa p = Pessoa.findById(pessoa.id);
        if (p == null) {
            throw new NotFoundException();
        }

        p.nome = pessoa.nome;
        p.anoNascimento = pessoa.anoNascimento;
        return p;

    }
    @DELETE
    @Transactional
    public void deletePessoa(int idPessoa) {
        boolean p = Pessoa.deleteById(idPessoa);
        if (!p) {
            throw new NotFoundException();
        }
    }
}
