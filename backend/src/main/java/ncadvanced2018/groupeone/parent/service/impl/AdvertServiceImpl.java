package ncadvanced2018.groupeone.parent.service.impl;

import lombok.extern.slf4j.Slf4j;
import ncadvanced2018.groupeone.parent.dao.AdvertDao;
import ncadvanced2018.groupeone.parent.dto.Feedback;
import ncadvanced2018.groupeone.parent.exception.EntityNotFoundException;
import ncadvanced2018.groupeone.parent.exception.NoSuchEntityException;
import ncadvanced2018.groupeone.parent.model.entity.Advert;
import ncadvanced2018.groupeone.parent.model.entity.AdvertType;
import ncadvanced2018.groupeone.parent.service.AdvertService;
import ncadvanced2018.groupeone.parent.service.impl.report.builder.SqlQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class AdvertServiceImpl implements AdvertService {

    private AdvertDao advertDao;

    @Autowired
    public AdvertServiceImpl(AdvertDao advertDao) {
        this.advertDao = advertDao;
    }

    @Override
    public Advert create(Advert advert) {
        if (advert == null) {
            log.info("Advert object is null in moment of creating");
            throw new EntityNotFoundException("Advert object is null");
        }
        if (advert.getType() == null) {
            log.info("AdvertType object is null in moment of creating an advert");
            throw new EntityNotFoundException("AdvertType object is null");
        }
        if (advert.getAdmin() == null) {
            log.info("Admin object is null in moment of creating an advert");
            throw new EntityNotFoundException("User object is null!");
        }
        return advertDao.create(advert);
    }

    @Override
    public Advert findById(Long id) {
        if (id <= 0) {
            log.info("Illegal id");
            throw new IllegalArgumentException();
        }
        return advertDao.findById(id);
    }

    @Override
    public List<Advert> findAll() {
        return advertDao.findAll();
    }

    public List<Feedback> findAllFeedback() {
        return advertDao.findAllFeedback();
    }

    @Override
    public List<Advert> findAllSorted(String field, boolean asc) {
        return advertDao.findAllSorted(buildOrderByCondition(field, asc));
    }

    @Override
    public List<Advert> findAllFilteredAndSorted(String field, boolean asc, String[] advertTypes) {
        return advertDao.findAllFilteredAndSorted(buildWhereCondition(advertTypes), buildOrderByCondition(field, asc));
    }

    private String buildWhereCondition(String[] roles) {
        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder
                .where()
                .in("type_id",
                        Arrays.stream(AdvertType.convertNamesToId(roles))
                                .map(Object::toString)
                                .toArray(String[]::new));
        return queryBuilder.build();
    }

    private String buildOrderByCondition(String sortedField, boolean asc) {
        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.orderBy();

        switch (sortedField) {
            case "id":
                queryBuilder.addField("id");
                break;
            case "header":
                queryBuilder.addField("header");
                break;
            case "type":
                queryBuilder.addField("type_id");
                break;
            case "admin":
                queryBuilder.addField("admin_id");
                break;
            case "dateOfPublishing":
                queryBuilder.addField("date_of_publishing");
                break;
            default:
                log.info("Illegal column " + sortedField);
                throw new IllegalArgumentException(sortedField);
        }

        if (!asc) {
            queryBuilder.desc();
        }

        return queryBuilder.build();
    }

    @Override
    public List<Advert> findAdvertsWithType(Long id) {
        return advertDao.findAdvertsWithType(id);
    }


    @Override
    public Advert update(Advert advert) {
        if (advert == null) {
            log.info("Advert object is null in moment of updating");
            throw new EntityNotFoundException("Advert object is null");
        }
        if (advert.getType() == null) {
            log.info("AdvertType object is null in moment of creating an advert");
            throw new EntityNotFoundException("AdvertType object is null");
        }
        if (advert.getAdmin() == null) {
            log.info("Admin object is null in moment of creating an advert");
            throw new EntityNotFoundException("User object is null!");
        }
        if (advertDao.findById(advert.getId()) == null) {
            log.info("No such advert entity");
            throw new NoSuchEntityException("Advert id is not found");
        }
        return advertDao.update(advert);
    }

    @Override
    public boolean delete(Advert advert) {
        if (advert == null) {
            log.info("Advert object is null in moment of deleting");
            throw new EntityNotFoundException("Advert object is null");
        }
        return advertDao.delete(advert);
    }

    @Override
    public boolean delete(Long id) {
        if (id <= 0) {
            log.info("Id less or equal zero");
            throw new IllegalArgumentException();
        }
        return advertDao.delete(id);
    }
}
