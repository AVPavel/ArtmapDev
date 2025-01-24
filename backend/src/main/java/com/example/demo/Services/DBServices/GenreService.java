package com.example.demo.Services.DBServices;

import com.example.demo.DBModels.Genre;
import com.example.demo.Exceptions.Models.DuplicateResourceException;
import com.example.demo.Exceptions.Models.GenreNotFoundException;
import com.example.demo.Repositories.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Transactional(readOnly = true)
    public Genre getGenreById(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException("Genre not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Genre getGenreByName(String name) {
        return genreRepository.findByName(name)
                .orElseThrow(() -> new GenreNotFoundException("Genre not found with name: " + name));
    }

    @Transactional(readOnly = true)
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    @Transactional
    public Genre addGenre(Genre genre) {
        if (genreRepository.findByName(genre.getName()).isPresent()) {
            throw new DuplicateResourceException("Genre with name '" + genre.getName() + "' already exists");
        }
        return genreRepository.save(genre);
    }

    @Transactional
    public Genre updateGenre(Long id, Genre updatedGenre) {
        Genre existingGenre = getGenreById(id);
        existingGenre.setName(updatedGenre.getName());
        return genreRepository.save(existingGenre);
    }

    @Transactional
    public void deleteGenre(Long id) {
        Genre genre = getGenreById(id);
        genreRepository.delete(genre);
    }
}
