package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.*;
import com.tallerwebi.infraestructura.RepositorioUsuarioImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.Mockito.*;

public class ControladorUsuarioTest {

	private RepositorioUsuarioImpl repositorioUsuario;
	private ServicioPropiedad servicioPropiedad;
	private ControladorUsuario controladorUsuario;
	private HttpSession session;
	private Usuario usuario;
	private List<Propiedad> propiedades;

	@BeforeEach
	public void init() {
		this.repositorioUsuario = mock(RepositorioUsuarioImpl.class);
		this.servicioPropiedad = mock(ServicioPropiedad.class);
		this.controladorUsuario = new ControladorUsuario(this.repositorioUsuario, this.servicioPropiedad);
		this.session = mock(HttpSession.class);

		this.usuario = new Usuario();
		this.usuario.setId(1L);
		this.propiedades = Arrays.asList(new Propiedad(), new Propiedad());

		when(this.session.getAttribute("usuario")).thenReturn(this.usuario);
		when(this.servicioPropiedad.listarPropiedades()).thenReturn(this.propiedades);
	}

	@Test
	public void queMuestreFavoritosDeUsuarioAutenticado() {
		Propiedad propiedad1 = new Propiedad();
		propiedad1.setNombre("Propiedad 1");
		Propiedad propiedad2 = new Propiedad();
		propiedad2.setNombre("Propiedad 2");

		Set<Propiedad> favoritos = new HashSet<>(Arrays.asList(propiedad1, propiedad2));

		when(this.repositorioUsuario.listarFavoritos(this.usuario)).thenReturn(favoritos);

		ModelAndView modelAndView = this.controladorUsuario.vistaFavoritos(this.session);

		assertThat(modelAndView.getViewName(), is("favoritos"));
		assertThat(modelAndView.getModel().get("listaFavoritos"), is(favoritos));
		assertThat(modelAndView.getModel().containsKey("listEmpty"), is(false));
	}


	@Test
	public void queMuestreMensajeCuandoNoHayFavoritos() {
		Set<Propiedad> favoritos = new HashSet<>();

		when(this.repositorioUsuario.listarFavoritos(this.usuario)).thenReturn(favoritos);

		ModelAndView modelAndView = this.controladorUsuario.vistaFavoritos(this.session);

		assertThat(modelAndView.getViewName(), is("favoritos"));
		assertThat(modelAndView.getModel().get("listaFavoritos"), is(favoritos));
		assertThat(modelAndView.getModel().get("listEmpty"), is("Todavia no has agregado propiedades a la lista de favoritos."));
	}


	@Test
	public void queRedirijaAlLoginAlIntentarAccederAFacoritosSinSesionIniciada() {

		when(this.session.getAttribute("usuario")).thenReturn(null);
		ModelAndView modelAndView = this.controladorUsuario.vistaFavoritos(this.session);

		assertThat(modelAndView.getViewName(), is("redirect:/login"));
	}


	@Test
	public void queMuestreErrorCuandoCRUDPropiedadExcepcion() {
		when(this.repositorioUsuario.listarFavoritos(this.usuario)).thenThrow(new CRUDPropiedadExcepcion("Error! La propiedad no pudo ser encontrada."));

		ModelAndView modelAndView = this.controladorUsuario.vistaFavoritos(this.session);

		assertThat(modelAndView.getViewName(), is("favoritos"));
		assertThat(modelAndView.getModel().get("error"), is("Error! La propiedad no pudo ser encontrada."));
	}


	@Test
	public void queMuestreErrorGeneralCuandoExcepcion() {
		when(this.repositorioUsuario.listarFavoritos(this.usuario)).thenThrow(new RuntimeException("Error inesperado"));

		ModelAndView modelAndView = this.controladorUsuario.vistaFavoritos(this.session);

		assertThat(modelAndView.getViewName(), is("favoritos"));
		assertThat(modelAndView.getModel().get("error"), is("Ha Ocurrido un Error Inesperado"));
	}


	@Test
	public void queAgregueFavoritoExitosamente() {
		Long propiedadId = 1L;

		ModelAndView modelAndView = this.controladorUsuario.agregarFavorito(propiedadId, this.session);

		assertThat(modelAndView.getViewName(), is("home"));
		assertThat(modelAndView.getModel().get("success"), is("La propiedad ha sido agregada a tu lista de favoritos correctamente!"));
		assertThat(modelAndView.getModel().get("propiedades"), is(this.propiedades));
		verify(this.repositorioUsuario).agregarFavorito(this.usuario, propiedadId);
	}

	@Test
	public void queAlAgregarUnaPropiedadFavoritosSinSesionIniciadaRedirijaAlLogin() {
		Long propiedadId = 1L;

		when(this.session.getAttribute("usuario")).thenReturn(null);
		ModelAndView modelAndView = this.controladorUsuario.agregarFavorito(propiedadId, this.session);

		assertThat(modelAndView.getViewName(), is("redirect:/login"));
	}


	@Test
	public void queMuestreErrorCuandoCRUDPropiedadExcepcionAlAgregar() {
		Long propiedadId = 1L;

		doThrow(new CRUDPropiedadExcepcion("Error! La propiedad no pudo ser encontrada.")).when(this.repositorioUsuario).agregarFavorito(this.usuario, propiedadId);

		ModelAndView modelAndView = this.controladorUsuario.agregarFavorito(propiedadId, this.session);

		assertThat(modelAndView.getViewName(), is("home"));
		assertThat(modelAndView.getModel().get("error"), is("Error! La propiedad no pudo ser encontrada."));
		assertThat(modelAndView.getModel().get("propiedades"), is(this.propiedades));
		verify(this.repositorioUsuario).agregarFavorito(this.usuario, propiedadId);
	}

	@Test
	public void queElimineFavoritoExitosamente() {
		Long propiedadId = 1L;

		ModelAndView modelAndView = this.controladorUsuario.eliminarFavorito(propiedadId, this.session);

		assertThat(modelAndView.getViewName(), is("home"));
		assertThat(modelAndView.getModel().get("success"), is("La propiedad ha sido eliminada de tu lista de favoritos correctamente."));
		verify(this.repositorioUsuario).eliminarFavorito(this.usuario, propiedadId);
	}

	@Test
	public void queMuestreErrorCuandoCRUDPropiedadExcepcionAlEliminar() {
		Long propiedadId = 1L;

		doThrow(new CRUDPropiedadExcepcion("Error! La propiedad no pudo ser encontrada.")).when(this.repositorioUsuario).eliminarFavorito(this.usuario, propiedadId);

		ModelAndView modelAndView = this.controladorUsuario.eliminarFavorito(propiedadId, this.session);

		assertThat(modelAndView.getViewName(), is("home"));
		assertThat(modelAndView.getModel().get("error"), is("Error! La propiedad no pudo ser encontrada."));
		verify(this.repositorioUsuario).eliminarFavorito(this.usuario, propiedadId);
	}
}
