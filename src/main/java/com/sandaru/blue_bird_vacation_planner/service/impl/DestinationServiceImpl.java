package com.sandaru.blue_bird_vacation_planner.service.impl;

import com.sandaru.blue_bird_vacation_planner.dto.DestinationRequest;
import com.sandaru.blue_bird_vacation_planner.dto.DestinationResponse;
import com.sandaru.blue_bird_vacation_planner.entity.Destination;
import com.sandaru.blue_bird_vacation_planner.entity.DestinationCategory;
import com.sandaru.blue_bird_vacation_planner.entity.DestinationImage;
import com.sandaru.blue_bird_vacation_planner.repository.DestinationRepository;
import com.sandaru.blue_bird_vacation_planner.service.DestinationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DestinationServiceImpl implements DestinationService {

    private final DestinationRepository destinationRepository;

    @Override
    public List<DestinationResponse> findAll(DestinationCategory category) {
        List<Destination> destinations = category == null
                ? destinationRepository.findAll()
                : destinationRepository.findAllByCategory(category);
        return destinations.stream().map(DestinationResponse::from).toList();
    }

    @Override
    public DestinationResponse findById(Long id) {
        return DestinationResponse.from(getDestination(id));
    }

    @Override
    public DestinationResponse create(DestinationRequest request) {
        return DestinationResponse.from(destinationRepository.save(toEntity(request)));
    }

    @Override
    public DestinationResponse update(Long id, DestinationRequest request) {
        Destination destination = getDestination(id);
        destination.setName(request.name());
        destination.setCategory(request.category());
        destination.setLatitude(request.latitude());
        destination.setLongitude(request.longitude());
        destination.setOpeningHour(request.openingHour());
        destination.setClosingHour(request.closingHour());

        if (request.imageUrls() != null) {
            destination.getImages().clear();

            List<DestinationImage> newImages = request.imageUrls().stream()
                    .map(url -> {
                        DestinationImage img = new DestinationImage();
                        img.setImageUrl(url);
                        img.setDestination(destination);
                        return img;
                    })
                    .toList();

            destination.getImages().addAll(newImages);
        }

        return DestinationResponse.from(destinationRepository.save(destination));
    }

    @Override
    public void delete(Long id) {
        if (!destinationRepository.existsById(id)) {
            throw notFound(id);
        }
        destinationRepository.deleteById(id);
    }

    private Destination getDestination(Long id) {
        return destinationRepository.findById(id).orElseThrow(() -> notFound(id));
    }

    private Destination toEntity(DestinationRequest request) {
        Destination destination = new Destination();
        destination.setName(request.name());
        destination.setCategory(request.category());
        destination.setLatitude(request.latitude());
        destination.setLongitude(request.longitude());
        destination.setOpeningHour(request.openingHour());
        destination.setClosingHour(request.closingHour());
        if (request.imageUrls() != null) {
            destination.getImages().clear();
            List<DestinationImage> newImages = request.imageUrls().stream()
                    .map(url -> {
                        DestinationImage img = new DestinationImage();
                        img.setImageUrl(url);
                        img.setDestination(destination);
                        return img;
                    })
                    .toList();
            destination.getImages().addAll(newImages);
        }
        return destination;
    }

    private ResponseStatusException notFound(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Destination not found: " + id);
    }
}
