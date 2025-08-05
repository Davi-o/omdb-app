import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Pagination, Container, Row, Col, Card, Form, Button, Spinner } from 'react-bootstrap';

export default function App() {
  const [title, setTitle] = useState('');
  const [search, setSearch] = useState('');
  const [results, setResults] = useState([]);
  const [type, setType] = useState('');
  const [year, setYear] = useState('');
  const [page, setPage] = useState(1);
  const [totalResults, setTotalResults] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);

  const fetchMovies = async () => {
    if (!search) return;
    setLoading(true);

    try {
      const params = new URLSearchParams();

      if (search) params.append('title', search);
      if (type) params.append('type', type);
      if (year) params.append('year', year);
      if (page) params.append('page', page);

      const res = await fetch(`${import.meta.env.VITE_API_URL}/search?${params.toString()}`);
      const data = await res.json();

      if (data && data.Search) {
        setResults(data.Search);
        setTotalResults(parseInt(data.totalResults, 10));
        setTotalPages(Math.ceil(data.totalResults / 10));
      } else {
        setResults([]);
        setTotalResults(0);
        setTotalPages(0);
      }
    } catch (error) {
      console.error(error);
    }

    setLoading(false);
  };

  useEffect(() => {
    fetchMovies();
  }, [search, type, year, page]);

  const handleSearch = (e) => {
    e.preventDefault();
    setPage(1);
    setSearch(title.trim());
  };

  return (
    <Container fluid className="py-4 text-white min-vh-100">
      <Form onSubmit={handleSearch} className="mb-4">
        <Row className="g-2 align-items-end">
          <Col xs={12} md={4}>
            <Form.Label>Título</Form.Label>
            <Form.Control
              type="text"
              placeholder="Digite um título"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
            />
          </Col>

          <Col xs={6} md={3}>
            <Form.Label>Tipo</Form.Label>
            <Form.Select value={type} onChange={(e) => setType(e.target.value)}>
              <option value="">Tudo</option>
              <option value="movie">Filmes</option>
              <option value="series">Séries</option>
              <option value="episode">Episódios</option>
            </Form.Select>
          </Col>

          <Col xs={6} md={2}>
            <Form.Label>Ano</Form.Label>
            <Form.Control
              type="number"
              placeholder="Ano"
              value={year}
              onChange={(e) => setYear(e.target.value)}
            />
          </Col>

          <Col xs={12} md={3}>
            <Button type="submit" variant="secondary" className="color-grey w-100 mt-3 mt-md-0">
              {loading ? '...' : 'Buscar'}
            </Button>
          </Col>
        </Row>
      </Form>

      {!loading && results.length === 0 && (
        <div className="text-center text-light mt-4">
          <h5>Nenhum resultado encontrado para sua busca.</h5>
          <p>Tente ajustar o título, tipo ou ano.</p>
        </div>
      )}

      {loading && (
        <div className="text-center text-light mt-4">
          <Spinner animation="border" variant="light" />
          <p>Buscando...</p>
        </div>
      )}

      {!loading && totalResults > 0 && (
        <Row>
          {results.map((movie) => (
            <Col key={movie.imdbID} sm={6} md={4} lg={3} className="mb-4">
              <Link to={`/movie/${movie.imdbID}`}>
                <Card className="h-100 bg-transparent text-white border-0 position-relative overflow-hidden">
                  <Card.Img
                    className="w-100 h-100 object-fit-cover"
                    style={{ minHeight: '300px' }}
                    src={movie.Poster !== 'N/A' ? movie.Poster : 'https://via.placeholder.com/300x445?text=Sem+Imagem'}
                  />
                  <Card.ImgOverlay className="bg-black bg-opacity-10 d-flex flex-column justify-content-end p-0">
                    <Card.Title className="bg-black bg-opacity-50 fw-bold fs-5 p-1">
                      {movie.Title} ({movie.Year})
                    </Card.Title>
                  </Card.ImgOverlay>
                </Card>
              </Link>
            </Col>
          ))}
        </Row>
      )}

      {!loading && totalResults > 0 && (
        <Pagination className="justify-content-center mt-4">
          <Pagination.Prev onClick={() => setPage((prev) => Math.max(prev - 1, 1))} disabled={page === 1} />
          <Pagination.Item active={page === 1} onClick={() => setPage(1)}>
            1
          </Pagination.Item>
          {Array.from({ length: totalPages }, (_, i) => i + 1)
            .filter((pageIndex) => pageIndex !== 1 && pageIndex !== totalPages && Math.abs(pageIndex - page) <= 3)
            .map((pageIndex) => (
              <Pagination.Item key={pageIndex} active={pageIndex === page} onClick={() => setPage(pageIndex)}>
                {pageIndex}
              </Pagination.Item>
            ))}
          {page < totalPages - 3 && <Pagination.Ellipsis disabled />}
          {page !== totalPages && (
            <Pagination.Item onClick={() => setPage(totalPages)}>{totalPages}</Pagination.Item>
          )}
          <Pagination.Next onClick={() => setPage((prev) => Math.min(prev + 1, totalPages))} disabled={page === totalPages} />
        </Pagination>
      )}
    </Container>
  );
}
