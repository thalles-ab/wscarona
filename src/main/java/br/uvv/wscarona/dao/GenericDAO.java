package br.uvv.wscarona.dao;

import java.io.Serializable;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import br.uvv.wscarona.model.BaseModel;
import br.uvv.wscarona.webservice.util.ListMessageException;

public class GenericDAO<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected ListMessageException erros; 

	@PersistenceContext
	protected EntityManager entityManager;
	 
	public GenericDAO() {
		erros = new ListMessageException();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public BaseModel merge(BaseModel entity) {
		this.entityManager.merge(entity);
		return entity;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public BaseModel searchById(BaseModel entity){
		try{
			return entityManager.find(entity.getClass(), entity.getId());
		}catch(NoResultException e){
			return null;
		}	
	}

	public void throwErros() throws ListMessageException{
		if(erros == null){
			return ;
		}
		if(!erros.getErros().isEmpty()){
			ListMessageException aux = new ListMessageException(erros.getErros());
			erros.clear();
			throw aux;
		}
	}

}
