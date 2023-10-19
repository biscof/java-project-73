package hexlet.code.service.label;

import hexlet.code.dto.LabelDto;
import hexlet.code.exception.DeletionException;
import hexlet.code.exception.LabelNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LabelServiceImpl implements LabelService {

    private LabelRepository labelRepository;

    @Override
    public Label getLabelById(Long id) {
        return labelRepository.findById(id).orElseThrow(
                () -> new LabelNotFoundException(id)
        );
    }

    @Override
    public List<Label> getAllLabeles() {
        return (List<Label>) labelRepository.findAll();
    }

    @Override
    public Label createLabel(LabelDto labelDto) {
        return labelRepository.save(new Label(labelDto.getName()));
    }

    @Override
    public Label updateLabel(Long id, LabelDto labelDto) {
        Label label = labelRepository.findById(id).orElseThrow(
                () -> new LabelNotFoundException(id)
        );
        label.setName(labelDto.getName());
        return labelRepository.save(label);
    }

    @Override
    public void deleteLabel(Long id) {
        Optional<Label> label = labelRepository.findById(id);

        if (label.isEmpty()) {
            throw new LabelNotFoundException(id);
        }

        boolean hasNoAssociatedTasks = label.get().getTasks().isEmpty();

        if (hasNoAssociatedTasks) {
            labelRepository.deleteById(id);
        } else {
            throw new DeletionException("Cannot delete label because there are tasks associated with it.");
        }
    }
}
