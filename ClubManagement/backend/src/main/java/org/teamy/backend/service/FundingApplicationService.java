package org.teamy.backend.service;

import org.teamy.backend.DataMapper.FundingApplicationMapper;
import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.Event;
import org.teamy.backend.model.FundingApplication;
import org.teamy.backend.model.exception.OptimisticLockingFailureException;
import org.teamy.backend.model.fundingApplicationStatus;
import org.teamy.backend.repository.*;
import org.teamy.backend.concurrent.LockManagerWait;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class FundingApplicationService {
    private final FundingApplicationRepository fundingApplicationRepository;
    private final ClubRepository clubRepository;
    private  final  DatabaseConnectionManager databaseConnectionManager;
    private static FundingApplicationService instance;
    public static synchronized FundingApplicationService getInstance(FundingApplicationRepository fundingApplicationRepository,ClubRepository clubRepository,DatabaseConnectionManager databaseConnectionManager) {
        if (instance == null) {
            instance = new FundingApplicationService(fundingApplicationRepository,clubRepository,databaseConnectionManager);
        }
        return instance;
    }
    private FundingApplicationService(FundingApplicationRepository fundingApplicationRepository, ClubRepository clubRepository,DatabaseConnectionManager databaseConnectionManager) {
        this.fundingApplicationRepository = fundingApplicationRepository;
        this.clubRepository = clubRepository;
        this.databaseConnectionManager = databaseConnectionManager;
    }

    public FundingApplication findFundingApplicationById(int id){
        Connection connection =null;
        try {

            connection= databaseConnectionManager.nextConnection();
            connection.setAutoCommit(false);

            return fundingApplicationRepository.findFundingApplicationsByIds(id,connection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {

        }
    }
    public boolean saveFundingApplication(FundingApplication fundingApplication, Connection conn) throws Exception {
        if (fundingApplication == null) {
            throw new IllegalArgumentException("Funding application cannot be null");
        }

        String clubId = String.valueOf(fundingApplication.getClubId());
        String threadName = Thread.currentThread().getName();

        try {
            // Start of thread-level locking
            LockManagerWait.getInstance().acquireLock(clubId, threadName);

            conn.setAutoCommit(false);
//            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            // 第二个判断：同一个 clubId 的资金申请在同一个 semester 不能重复
            int duplicateInSameSemester = fundingApplicationRepository.existsByClubIdAndSemester(fundingApplication.getClubId(), fundingApplication.getSemester(), conn);
            if (duplicateInSameSemester > 0) {
                throw new IllegalStateException("A funding application already exists for this club and semester.");
            }

            // Save the funding application
            boolean isSuccess = fundingApplicationRepository.saveFundingApplication(fundingApplication, conn);

            // Commit the transaction
            conn.commit();

            return isSuccess;
        } catch (SQLException e) {
            conn.rollback();
            throw new RuntimeException("Error saving funding application: " + e.getMessage(), e);
        } finally {
            // Release the thread-level lock
            LockManagerWait.getInstance().releaseLock(clubId, threadName);
            conn.setAutoCommit(true);
        }
    }


    public List<FundingApplication> getAllFundingApplication() {
        try {
            return fundingApplicationRepository.getAllFundingApplication();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void reviewFundingApplication(int applicationId,int reviewerId,String stat){
        Connection connection = null;
        FundingApplication fundingApplication = null;
        try {
            // 获取数据库连接
            connection = databaseConnectionManager.nextConnection();
            // 禁止自动提交，开启事务
            connection.setAutoCommit(false);

            // 获取 fundingApplication 对象
            fundingApplication = fundingApplicationRepository.findFundingApplicationsByIds(applicationId, connection);

            if (fundingApplication == null) {
                throw new IllegalArgumentException("Funding application not found for id: " + applicationId);
            }

            // 第一个判断：资金申请的状态必须是 Reviewed，才能进行审批
            if (fundingApplication.getStatus() != fundingApplicationStatus.Submitted) {
                throw new IllegalStateException("Funding application is not in 'Submitted' status.");
            }

            // 第二个判断：同一个 clubId 的资金申请在同一个 semester 不能重复
            int DuplicateInSameSemester = fundingApplicationRepository.existsByClubIdAndSemester(fundingApplication.getClubId(), fundingApplication.getSemester(), connection);

            if (DuplicateInSameSemester>1) {
                throw new IllegalStateException("A funding application already exists for this club and semester.");
            }

            // 审核资金申请，更新状态
            boolean isSuccess = fundingApplicationRepository.reviewFundingApplication(reviewerId, fundingApplication, stat, connection);

            if (!isSuccess) {
                throw new OptimisticLockingFailureException("Failed to review the funding application because of optimistic lock.");
            }

            // 提交事务
            connection.commit();
        }catch (OptimisticLockingFailureException e) {
            // 出现异常时回滚事务
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw new OptimisticLockingFailureException("conflict: " + e.getMessage());
        } catch (SQLException e) {
            // 出现异常时回滚事务
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw new RuntimeException("Error reviewing funding application: " + e.getMessage(), e);
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);  // 恢复自动提交模式
                    connection.close();  // 关闭连接
                }
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    public boolean updateFundingApplication(FundingApplication fundingApplication) throws Exception {
        Connection connection = null;
        try {
            // 获取数据库连接
            connection = databaseConnectionManager.nextConnection();

            // 禁止自动提交，开启事务
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

            // 获取当前 fundingApplication 的最新版本，确保使用正确的版本号
            FundingApplication fundingApplicationFromDb = fundingApplicationRepository.findFundingApplicationsByIds(fundingApplication.getId(), connection);

            // 将数据库中的版本号赋值给传入的对象，以便后续的乐观锁检查
            fundingApplication.setVersion(fundingApplicationFromDb.getVersion());

            // 第一个判断：资金申请的状态必须没被审批，才能进行更新
            if (fundingApplication.getStatus() != fundingApplicationStatus.Submitted) {
                throw new IllegalStateException("Funding application is not in 'Submitted' status.");
            }

            // 第二个判断：同一个 clubId 的资金申请在同一个 semester 不能重复
            int DuplicateInSameSemester = fundingApplicationRepository.existsByClubIdAndSemester(fundingApplication.getClubId(), fundingApplication.getSemester(), connection);
            if (DuplicateInSameSemester>1) {
                throw new IllegalStateException("A funding application already exists for this club and semester.");
            }

            // 更新资金申请，使用乐观锁机制
            boolean isSuccess = fundingApplicationRepository.updateFundingApplication(fundingApplication, connection);

            if (!isSuccess) {
                throw new RuntimeException("Failed to update the funding application.");
            }

            // 提交事务
            connection.commit();

            return true;
        } catch (SQLException e) {
            // 在出现异常时回滚事务
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw new RuntimeException("Error updating funding application: " + e.getMessage(), e);
        }catch (OptimisticLockingFailureException e){
            // 在出现异常时回滚事务
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw new OptimisticLockingFailureException("Error updating funding application: " + e.getMessage());
        } finally {
            // 确保在任何情况下都关闭数据库连接
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);  // 恢复自动提交模式
                    connection.close();  // 关闭连接
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }
}
