package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.Metaproject;
import main.java.org.htwg.konstanz.metaproject.repositories.MetaprojectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import static org.apache.commons.lang3.StringUtils.trim;

/**
 * Implementation for metaproject data access object.
 *
 * @author SiKelle
 */
@Service
public class MetaprojectDAOImpl implements MetaprojectDAO {

    private static final Logger log = LoggerFactory.getLogger(MetaprojectDAOImpl.class);

    private final MetaprojectRepository metaprojectRepo;

    public MetaprojectDAOImpl(MetaprojectRepository metaprojectRepo) {
        this.metaprojectRepo = metaprojectRepo;
    }

    @Override
    public Metaproject findById(Long id) {
        return metaprojectRepo.findById(id).orElse(null);
    }

    @Override
    public Metaproject save(Metaproject metaproject) {
        metaproject.setMetaprojectTitle(trim(metaproject.getMetaprojectTitle()));
        return metaprojectRepo.save(metaproject);
    }

    @Override
    public Metaproject update(Long metaprojectId, Metaproject transientMetaproject) {
        Metaproject metaproject = findById(metaprojectId);
        if (metaproject != null)
            transientMetaproject.setMetaprojectId(metaprojectId);
        return metaprojectRepo.save(transientMetaproject);
    }

    @Override
    public Metaproject delete(Long metaprojectId) {
        Metaproject metaproject = findById(metaprojectId);
        if (metaproject != null)
            metaprojectRepo.delete(metaproject);
        return metaproject;
    }

    @Override
    public Collection<Metaproject> findAll() {
        return metaprojectRepo.findAll();
    }

    @Override
    public Page<Metaproject> findAll(int page, int size, String sortAttribute, boolean isDescending) {
        Pageable pageable;
        if (isDescending)
            pageable = PageRequest.of(page, size, Sort.by(sortAttribute).descending());
        else
            pageable = PageRequest.of(page, size, Sort.by(sortAttribute).ascending());
        return metaprojectRepo.findAll(pageable);
    }

    @Override
    public Collection<Metaproject> findAllVisible(int page, int size, String sortAttribute, boolean isDescending) {
        Pageable pageable;
        if (isDescending)
            pageable = PageRequest.of(page, size, Sort.by(sortAttribute).descending());
        else
            pageable = PageRequest.of(page, size, Sort.by(sortAttribute).ascending());

        return metaprojectRepo.findAllVisible(pageable).getContent();
    }

    @Override
    public Collection<Metaproject> findAllVisible() {
        Collection<Metaproject> metaprojects = this.findAll();
        return metaprojects.stream().filter(Metaproject::isVisible).collect(Collectors.toSet());
    }

    @Override
    public Collection<Metaproject> findByRegisterType(String registerType) {
        Collection<Metaproject> metaprojects = this.findAll();

        Collection<Metaproject> metaprojectsResponse = new HashSet<>();
        for (Metaproject mp : metaprojects) {
            if (!(mp.getRegisterType().equals(registerType)))
                metaprojectsResponse.add(mp);
        }
        return metaprojectsResponse;
    }

    @Override
    public Collection<Metaproject> findAllNonPreRegistration() {
        Collection<Metaproject> metaprojects = this.findAll();

        Collection<Metaproject> nonPreRegMetaprojects = new HashSet<>();
        for (Metaproject mp : metaprojects) {
            if (!(mp.getPreRegistration()))
                nonPreRegMetaprojects.add(mp);
        }
        return nonPreRegMetaprojects;
    }

}
