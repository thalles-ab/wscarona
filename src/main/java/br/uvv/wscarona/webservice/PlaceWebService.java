package br.uvv.wscarona.webservice;

import java.util.List;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import br.uvv.wscarona.dao.PlaceDAO;
import br.uvv.wscarona.model.Place;
import br.uvv.wscarona.model.enumerator.TypeSituation;
import br.uvv.wscarona.webservice.util.ListMessageException;

@Path("/place")
@RequestScoped
public class PlaceWebService extends BaseWebService {
	@Inject private PlaceDAO placeDAO;

    @POST
    public Response SaveOrUpdate(String request) throws ListMessageException {
        try{
            //TODO - Conversão só funciona quando o enum é digitado como String (Ex: "Casa" - Como seu valor, 0, a conversão não funciona)
            //TODO - Precisamos expor alguma chave unica (ou mesmo o ID) para termos uma referência de unicidade da tabela (necessário para update)
            //No momento, apenas cria, pois não tem referência de PK ou UK.
            Place place = gson.fromJson(request, Place.class);
            place.setStudent(studentContext);
            place.setSituation(TypeSituation.Enable);
            return successRequest(placeDAO.saveOrUpdate(place));
        }
        catch (ListMessageException e){
            return badRequest(erros);
        }
    }

	@GET
	public Response getAll(String json) {
		List<Place> placesList = placeDAO.getPlaces(studentContext);
        return successRequest(placesList);
	}

    @GET
    @Path("/{id}")
    public Response getSpecific(@PathParam("id") String id) throws ListMessageException {
        Place place= placeDAO.getPlace(id);
        return successRequest(place);
    }

    @GET
    @Path("/search/{geoLocalization}")
    public Response getByGeolocalization(@PathParam("geoLocalization") String geoLocalization) throws ListMessageException {
        try {
            List<Place> results = placeDAO.getPlaceByGeo(geoLocalization);
            return successRequest(results);
        }
        catch (Exception e){
            return badRequest(erros);
        }
    }

    //Bug: Delete usando "Delete" não funciona usando parâmetros, pelo que li é um bug conhecido.
    //TODO - Usuário poderá deletar qualquer place ou apenas os place em que seja "dono" ? (Da maneira implementada, apenas os próprios locais)
    @GET
    @Path("/delete/{id}")
    public Response delete(@PathParam("id") String id){
        try{
            if(Objects.equals(placeDAO.getPlace(id).getStudent().getCode(),studentContext.getCode())){
                placeDAO.delete(id);
            }
            else{
                throw new Exception("Só pode excluir local do domínio do usuário logado.");
            }
            //TODO - Qual resposta do sucesso de delete ?
            return successRequest("Success");
        }
        catch (Exception e){
            return badRequest(erros);
        }
    }
}
