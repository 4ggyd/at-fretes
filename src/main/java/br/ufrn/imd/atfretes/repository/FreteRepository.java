package br.ufrn.imd.atfretes.repository;

import br.ufrn.imd.atfretes.model.Frete;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class FreteRepository {

    private List<Frete> fretes = new ArrayList<>();

    public void salvar(Frete frete) {
        fretes.add(frete);
    }

    public List<Frete> listarTodos() {
        return fretes;
    }
}