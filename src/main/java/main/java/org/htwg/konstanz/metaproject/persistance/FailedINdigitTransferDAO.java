package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.FailedINdigitTransfer;
import main.java.org.htwg.konstanz.metaproject.entities.Project;
import main.java.org.htwg.konstanz.metaproject.services.INdigitApiService;

import java.io.IOException;
import java.util.List;

public interface FailedINdigitTransferDAO {

    List<FailedINdigitTransfer> findAll();

    /**
     * use this method if the transfer of the project and its members failed
     * (both transfers)
     */
    FailedINdigitTransfer newFailedProjectTransfer(Project project, long statusCode);

    /**
     * use this method if only the transfer of the members of the project failed
     */
    FailedINdigitTransfer newFailedProjectMembersTransfer(Project project, long statusCode);

    /**
     * retry the transfer to Indigit
     *
     * @return statusCode of transfer
     */
    int retryTransfer(FailedINdigitTransfer failedTransfer) throws INdigitApiService.HttpStatusCodeException, IOException;

    /**
     * try the transfer to Indigit
     *
     * @return statusCode of transfer
     */
    int tryTransfer(Project project, String timeStamp) throws INdigitApiService.HttpStatusCodeException, IOException;

}