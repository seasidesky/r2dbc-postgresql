/*
 * Copyright 2017-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.r2dbc.postgresql.codec;

import io.r2dbc.postgresql.client.Parameter;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.r2dbc.postgresql.message.Format.BINARY;
import static io.r2dbc.postgresql.message.Format.TEXT;
import static io.r2dbc.postgresql.type.PostgresqlObjectId.MONEY;
import static io.r2dbc.postgresql.type.PostgresqlObjectId.NUMERIC;
import static io.r2dbc.postgresql.type.PostgresqlObjectId.VARCHAR;
import static io.r2dbc.postgresql.util.ByteBufUtils.encode;
import static io.r2dbc.postgresql.util.TestByteBufAllocator.TEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

final class BigDecimalCodecTest {

    @Test
    void constructorNoByteBufAllocator() {
        assertThatNullPointerException().isThrownBy(() -> new BigDecimalCodec(null))
            .withMessage("byteBufAllocator must not be null");
    }

    @Test
    void decode() {
        BigDecimal bigDecimal = new BigDecimal("100");

        assertThat(new BigDecimalCodec(TEST).decode(encode(TEST, bigDecimal.toString()), TEXT, BigDecimal.class))
            .isEqualTo(bigDecimal);
    }

    @Test
    void decodeNoByteBuf() {
        assertThat(new BigDecimalCodec(TEST).decode(null, TEXT, BigDecimal.class)).isNull();
    }

    @Test
    void doCanDecode() {
        BigDecimalCodec codec = new BigDecimalCodec(TEST);

        assertThat(codec.doCanDecode(BINARY, NUMERIC)).isFalse();
        assertThat(codec.doCanDecode(TEXT, MONEY)).isFalse();
        assertThat(codec.doCanDecode(TEXT, NUMERIC)).isTrue();
    }

    @Test
    void doCanDecodeNoFormat() {
        assertThatNullPointerException().isThrownBy(() -> new BigDecimalCodec(TEST).doCanDecode(null, VARCHAR))
            .withMessage("format must not be null");
    }

    @Test
    void doCanDecodeNoType() {
        assertThatNullPointerException().isThrownBy(() -> new BigDecimalCodec(TEST).doCanDecode(TEXT, null))
            .withMessage("type must not be null");
    }

    @Test
    void doEncode() {
        BigDecimal bigDecimal = new BigDecimal("100");

        assertThat(new BigDecimalCodec(TEST).doEncode(bigDecimal))
            .isEqualTo(new Parameter(TEXT, NUMERIC.getObjectId(), encode(TEST, "100")));
    }

    @Test
    void doEncodeNoValue() {
        assertThatNullPointerException().isThrownBy(() -> new BigDecimalCodec(TEST).doEncode(null))
            .withMessage("value must not be null");
    }

    @Test
    void encodeNull() {
        assertThat(new BigDecimalCodec(TEST).encodeNull())
            .isEqualTo(new Parameter(TEXT, NUMERIC.getObjectId(), null));
    }

}
