package site.archive.domain.archive;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import site.archive.domain.archive.entity.Archive;
import site.archive.domain.archive.entity.Emotion;
import site.archive.domain.user.entity.UserRole;
import site.archive.domain.user.info.UserInfo;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ArchiveServiceTest {

    private static final long USER_ID = 1L;
    @Mock
    ArchiveRepository archiveRepository;
    private ArchiveService archiveService;

    @BeforeEach
    void setUp() {
        archiveService = new ArchiveService(archiveRepository);
    }

    @DisplayName("특정 USER의 모든 Archive를 가져온다")
    @Test
    void returnAllArchives() {
        // given
        var userInfo = new UserInfo("dummy@test.com", UserRole.GENERAL, USER_ID);
        var archives = dummyArchives(USER_ID);
        given(archiveRepository.findAllByAuthorId(USER_ID)).willReturn(archives);

        // when
        var allArchives = archiveService.getAllArchive(userInfo);

        // then
        assertThat(allArchives.getArchiveCount()).isEqualTo(archives.size());
        allArchives.getArchives()
                   .forEach(archiveDto -> assertThat(archiveDto.getAuthorId()).isEqualTo(USER_ID));
    }

    @DisplayName("작성한 게시물이 없는 경우, 빈 리스트가 반환된다")
    @Test
    void returnEmptyArchiveListWhenNotExistArchive() {
        // given
        var userInfo = new UserInfo("dummy@test.com", UserRole.GENERAL, USER_ID);
        given(archiveRepository.findAllByAuthorId(USER_ID)).willReturn(Collections.emptyList());

        // when
        var allArchives = archiveService.getAllArchive(userInfo);

        // then
        assertThat(allArchives.getArchiveCount()).isZero();
    }

    @DisplayName("자신의 아카이브인 경우, public/private 상관없이 조회가 가능하다")
    @Test
    void canViewAllArchiveWhenMyArchive() {
        // given
        var userInfo = new UserInfo("dummy@test.com", UserRole.GENERAL, USER_ID);
        var archives = dummyArchives(USER_ID);
        given(archiveRepository.findAllByAuthorId(USER_ID)).willReturn(archives);

        // when
        var allArchives = archiveService.getAllArchive(userInfo, USER_ID);

        // then
        assertThat(allArchives.getArchiveCount()).isEqualTo(archives.size());
        allArchives.getArchives()
                   .forEach(archiveDto -> assertThat(archiveDto.getAuthorId()).isEqualTo(USER_ID));
    }

    @DisplayName("타인 아카이브인 경우, public 아카이브만 조회가 가능하다")
    @Test
    void canViewOnlyPublicArchiveWhenOtherArchive() {
        // given
        var myUserID = 99L;
        var userInfo = new UserInfo("dummy@test.com", UserRole.GENERAL, myUserID);
        var archives = dummyArchives(USER_ID);
        given(archiveRepository.findAllByAuthorId(USER_ID)).willReturn(archives);

        // and public archive count
        var archivePublicCount = archives.stream()
                                         .filter(archive -> archive.getAuthorId() == userInfo.getUserId() || archive.getIsPublic())
                                         .count();

        // when
        var allArchives = archiveService.getAllArchive(userInfo, USER_ID);

        // then
        assertThat(allArchives.getArchiveCount()).isEqualTo(archivePublicCount);
        allArchives.getArchives()
                   .forEach(archiveDto -> {
                       assertThat(archiveDto.getIsPublic()).isTrue();
                       assertThat(archiveDto.getAuthorId()).isEqualTo(USER_ID);
                   });
    }

    private List<Archive> dummyArchives(long authorId) {
        return List.of(
            Archive.builder()
                   .name("archive_1")
                   .authorId(authorId)
                   .watchedOn(LocalDate.now())
                   .emotion(Emotion.PLEASANT)
                   .mainImage("main_image_1")
                   .companions(Collections.emptyList())
                   .isPublic(true)
                   .build(),
            Archive.builder()
                   .name("archive_2")
                   .authorId(authorId)
                   .watchedOn(LocalDate.now())
                   .emotion(Emotion.AMAZING)
                   .mainImage("main_image_2")
                   .companions(Collections.emptyList())
                   .isPublic(false)
                   .build()
        );

    }

}