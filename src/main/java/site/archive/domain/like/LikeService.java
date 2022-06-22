package site.archive.domain.like;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.archive.domain.common.BaseTimeEntity;
import site.archive.domain.like.entity.Like;
import site.archive.exception.common.ResourceNotFoundException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    @Transactional
    public void save(Long userId, Long archiveId) {
        likeRepository.findByUserIdAndArchiveId(userId, archiveId)
                      .ifPresentOrElse(BaseTimeEntity::deleteCancel,
                                       () -> likeRepository.save(Like.of(userId, archiveId)));
    }

    @Transactional
    public void delete(Long userId, Long archiveId) {
        likeRepository.findByUserIdAndArchiveId(userId, archiveId)
                      .ifPresentOrElse(likeRepository::delete,
                                       () -> {throw new ResourceNotFoundException("조건에 맞는 Like 데이터가 없습니다");});
    }

}
