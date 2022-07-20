package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.CompInvitationCode;
import com.linzhi.gongfu.entity.CompInvitationCodeId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CompInvitationCodeRepository extends CrudRepository<CompInvitationCode, CompInvitationCodeId>, QuerydslPredicateExecutor<CompInvitationCode> {


    Optional<CompInvitationCode> findCompInvitationCodeByCompInvitationCodeId_InvitationCode(String invitationCode);

    void deleteByCompInvitationCodeId_InvitationCode(String invitationCode);
}
