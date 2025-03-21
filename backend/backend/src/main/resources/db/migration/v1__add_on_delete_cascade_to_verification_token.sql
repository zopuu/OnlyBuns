-- V1__add_on_delete_cascade_to_verification_token.sql
ALTER TABLE verification_token
DROP CONSTRAINT fk3asw9wnv76uxu3kr1ekq4i1ld;

ALTER TABLE verification_token
    ADD CONSTRAINT fk3asw9wnv76uxu3kr1ekq4i1ld
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE;
