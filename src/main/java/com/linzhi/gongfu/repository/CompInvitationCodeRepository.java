package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.CompInvitationCode;
import com.linzhi.gongfu.entity.CompInvitationCodeId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CompInvitationCodeRepository extends CrudRepository<CompInvitationCode, CompInvitationCodeId>, QuerydslPredicateExecutor<CompInvitationCode> {

    /**
     * 查找邀请码详情
     *
     * @param invitationCode 邀请码
     * @return 邀请码详情
     */
    Optional<CompInvitationCode> findCompInvitationCodeByCompInvitationCodeId_InvitationCode(String invitationCode);

    /**
     * 删除邀请码信息
     *
     * @param invitationCode 邀请码
     */
    void deleteByCompInvitationCodeId_InvitationCode(String invitationCode);

    /**
     * 查找邀请码详情
     *
     * @param dcCompId 单位编码
     * @return 邀请码详情
     */
    Optional<CompInvitationCode> findCompInvitationCodeByCompInvitationCodeId_DcCompId(String dcCompId);

}
