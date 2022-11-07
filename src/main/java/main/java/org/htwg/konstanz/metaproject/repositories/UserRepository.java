package main.java.org.htwg.konstanz.metaproject.repositories;

import main.java.org.htwg.konstanz.metaproject.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserName(String username);

    @Query("SELECT u FROM User u WHERE u.userName LIKE '%_inactive%'")
    Page<User> getInactiveUserPaginated(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.userName NOT LIKE '%_inactive%'")
    Page<User> getActiveUserPaginated(Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.userName LIKE CONCAT('%',:username,'_inactive%')")
    List<BigInteger> getInactiveUserCount(@Param("username") String username);

    @Query("SELECT MAX(u.userId) FROM User u")
    List<BigInteger> getMaxUserId();

    /**
     * Find user by userName, firstName, lastName or userMail. This method
     * returns a list of maximal n users order by userName asc.
     * <p>
     * If you want to search a String as a substring of another string
     * then please insert "%" + searchString + "%" for the param searchString
     */
    @Query("SELECT u FROM User u WHERE " +
            "u.userName LIKE :searchString " +
            "OR u.userFirstName LIKE :searchString " +
            "OR u.userLastName LIKE :searchString " +
            "OR u.userEmail LIKE :searchString " +
            "ORDER BY u.userName ASC")
    //LIMIT :max" -> native query?
    List<User> findBySearchString(String searchString);

    /**
     * @return User that have a #RelationMetaprojectUser with metaId, but
     * no relTeamUser or a relTeamUser with status 1 or 0
     */
    @Query("SELECT u " +
            "FROM RelationMetaprojectUser relMetaUser " +
            "LEFT OUTER JOIN User u ON relMetaUser.userId.userId = u.userId " +
            "LEFT OUTER JOIN RelationTeamUser relTeamUser ON relTeamUser.userId.userId = u.userId " +
            "WHERE " +
            "(relTeamUser.relationTeamUserId IS NULL OR relTeamUser.teamMemberStatus = 1 OR relTeamUser.teamMemberStatus = 0) " +
            "AND relTeamUser.teamId.metaProjectId.metaProjectId = :metaId " +
            "AND relMetaUser.metaProjectId.metaProjectId = :metaId ")
    List<User> findByMetaprojectTeamLess(Long metaId);

}
