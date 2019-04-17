package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.AppointmentResource;
import org.openmrs.module.appointmentscheduling.BlockExcludedDays;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SubResource(parent = AppointmentResourceResource1_9.class, path = "blockexcludeddays", supportedClass = BlockExcludedDays.class, supportedOpenmrsVersions = {"2.0.*, 2.1.*, 2.2.*, 2.4.*, 2.8.*"})
public class BlockExcludedDaysResource1_9 extends DelegatingSubResource<BlockExcludedDays, AppointmentResource, AppointmentResourceResource1_9> {


    @Override
    public BlockExcludedDays getByUniqueId(String uniqueId) {
        return Context.getService(AppointmentService.class).geyExcludedDayByUuid(uniqueId);
    }

    @Override
    public BlockExcludedDays newDelegate() {
        return new BlockExcludedDays();
    }


    @Override
    public BlockExcludedDays save(BlockExcludedDays delegate) {
        log.error("excluded days  save");
        AppointmentResource parent = delegate.getAppointmentResource();
        parent.addExcludedDays(delegate);
        Context.getService(AppointmentService.class).saveAppointmentResource(parent);
        return delegate;
    }

    @Override
    public AppointmentResource getParent(BlockExcludedDays instance) {
        return instance.getAppointmentResource();
    }

    @Override
    public void setParent(BlockExcludedDays instance, AppointmentResource parent) {
        instance.setAppointmentResource(parent);
    }

    @Override
    public PageableResult doGetAll(AppointmentResource parent, RequestContext context) throws ResponseException {
        log.error("excluded days  doGetAll");
        List<BlockExcludedDays> days = new ArrayList<BlockExcludedDays>();
        if (parent != null) {
            for (BlockExcludedDays day : parent.getDaysList()) {
                days.add(day);
            }
        }
        return new NeedsPaging<BlockExcludedDays>(days, context);
    }

    @Override
    protected void delete(BlockExcludedDays delegate, String reason, RequestContext context) throws ResponseException {

    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        if (rep instanceof DefaultRepresentation) {
            description.addProperty("uuid");
            description.addProperty("display", findMethod("getDisplayString"));
            description.addProperty("excludedDate");
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof FullRepresentation) {
            description.addProperty("uuid");
            description.addProperty("display", findMethod("getDisplayString"));
            description.addProperty("excludedDate");
            description.addProperty("auditInfo", findMethod("getAuditInfo"));
            description.addSelfLink();
            return description;
        } else if (rep instanceof RefRepresentation) {
            description.addProperty("uuid");
            description.addProperty("display", findMethod("getDisplayString"));
            description.addProperty("excludedDate");
            description.addSelfLink();
            return description;
        }
        return null;
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("excludedDate");
        return description;
    }

    @Override
    public DelegatingResourceDescription getUpdatableProperties() {
        return getCreatableProperties();
    }

    @Override
    public void purge(BlockExcludedDays delegate, RequestContext context) throws ResponseException {

    }


    @PropertyGetter("display")
    public String getDisplayString(BlockExcludedDays bdays) {
        return bdays.getAppointmentResource() + ": "
                + bdays.getExcludedDate() + " - " + bdays.getCreator();
    }

}