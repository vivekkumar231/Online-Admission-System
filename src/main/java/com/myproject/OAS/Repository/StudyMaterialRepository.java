package com.myproject.OAS.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.myproject.OAS.Model.StudyMaterial;
import com.myproject.OAS.Model.StudyMaterial.MaterialType;

@Repository
public interface StudyMaterialRepository extends JpaRepository<StudyMaterial, Long> {

    // Count materials by specific filters
    long countByMaterialTypeAndProgramAndBranchAndYear(
            MaterialType materialType,
            String program,
            String branch,
            String year
    );

    // Fetch all materials matching the given filters
    List<StudyMaterial> findAllByMaterialTypeAndProgramAndBranchAndYear(
            MaterialType materialType,
            String program,
            String branch,
            String year
    );

    // Optional: if you also want to filter only by type
    List<StudyMaterial> findAllByMaterialType(MaterialType materialType);
}
