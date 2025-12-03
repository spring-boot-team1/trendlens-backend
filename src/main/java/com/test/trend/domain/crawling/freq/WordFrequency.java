package com.test.trend.domain.crawling.freq;

import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "WordFrequency")
@SequenceGenerator(
        name = "seqWordFreqGenerator",
        sequenceName = "seqWordFreq",
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
public class WordFrequency {

}
